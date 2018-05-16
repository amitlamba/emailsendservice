package com.und.service

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.Body
import com.amazonaws.services.simpleemail.model.Content
import com.amazonaws.services.simpleemail.model.Destination
import com.amazonaws.services.simpleemail.model.SendEmailRequest
import com.und.eventapi.repository.EventUserRepository
import com.und.factory.EmailServiceProviderConnectionFactory
import com.und.model.jpa.EmailTemplate
import com.und.model.jpa.Status
import com.und.model.mongo.EmailStatus
import com.und.model.mongo.EmailStatusUpdate
import com.und.model.utils.*
import com.und.repository.EmailSentRepository
import com.und.repository.EmailTemplateRepository
import com.und.repository.ServiceProviderCredentialsRepository
import com.und.utils.TenantProvider
import com.und.utils.loggerFor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.MimeMessage
import com.amazonaws.services.simpleemail.model.Message as SESMessage


@Service
class EmailSendService {
    companion object {
        protected val logger = loggerFor(EmailSendService::class.java)
    }

    @Autowired
    private lateinit var serviceProviderCredentialsRepository: ServiceProviderCredentialsRepository

    @Autowired
    private lateinit var serviceProviderCredentialsService: ServiceProviderCredentialsService

    @Autowired
    private lateinit var emailSentRepository: EmailSentRepository

    @Autowired
    private lateinit var emailServiceProviderConnectionFactory: EmailServiceProviderConnectionFactory

    @Autowired
    private lateinit var eventUserRepository: EventUserRepository

    @Autowired
    private lateinit var emailTemplateRepository: EmailTemplateRepository

    @Autowired
    private lateinit var templateContentCreationService: TemplateContentCreationService

    private var wspCredsMap: MutableMap<Long, ServiceProviderCredentials> = mutableMapOf()

    fun sendEmailByAWSSDK(emailSESConfig: EmailSESConfig, email: Email) {
        val credentialsProvider: AWSCredentialsProvider = AWSStaticCredentialsProvider(BasicAWSCredentials(emailSESConfig.awsAccessKeyId, emailSESConfig.awsSecretAccessKey))
        try {
            val client = AmazonSimpleEmailServiceClientBuilder.standard()
                    .withCredentials(credentialsProvider)
                    // Replace US_WEST_2 with the AWS Region you're using for
                    // Amazon SES.
                    .withRegion(emailSESConfig.region).build()
            val request = SendEmailRequest()
            with(request) {
                destination = Destination().withToAddresses(
                        (email.toEmailAddresses.map { it.address }.toMutableList()))

                message = (SESMessage()
                        .withBody(Body()
                                .withHtml(Content()
                                        .withCharset("UTF-8").withData(email.emailBody))
                        )
                        .withSubject(Content()
                                .withCharset("UTF-8").withData(email.emailSubject)))

                source = email.fromEmailAddress.address

//                configurationSetName = emailSESConfig.CONFIGSET
            }
            client.sendEmail(request)
            logger.debug("Email sent!")
        } catch (ex: Exception) {
            logger.error("The email was not sent. Error message: " + ex.message)
        }
        this.saveMailInMongo(email, com.und.model.mongo.EmailStatus.SENT)
    }

    fun sendEmailBySMTP(emailSMTPConfig: EmailSMTPConfig? = null, email: Email) {

        val session = emailServiceProviderConnectionFactory.getSMTPSession(email.clientID, emailSMTPConfig)

        val transport = emailServiceProviderConnectionFactory.getSMTPTransportConnection(email.clientID)


        // Send the message.
        try {
            logger.debug("Sending...")
            val mongoEmailId = this.saveMailInMongo(email, com.und.model.mongo.EmailStatus.NOT_SENT)

            val msg = createMimeMessage(session, email, emailServiceProviderConnectionFactory.getEmailServiceProvider(email.clientID))
            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients())
//            transport.sendMessage(msg, msg.getAllRecipients())
            println("Email sent!")
        } catch (ex: Exception) {
            logger.error("The email was not sent.")
            logger.error("Error message: " + ex.message)
        } finally {
            // Close and terminate the connection.
            emailServiceProviderConnectionFactory.closeSMTPTransportConnection(email.clientID)
        }

        this.saveMailInMongo(email, com.und.model.mongo.EmailStatus.SENT)
    }

    private fun createMimeMessage(session: Session, email: Email, emailSMTPConfig: EmailSMTPConfig): MimeMessage {
        // Create a message with the specified information.
        val msg = MimeMessage(session)
        msg.setFrom(email.fromEmailAddress)
        msg.setRecipients(Message.RecipientType.TO, email.toEmailAddresses)
        msg.setRecipients(Message.RecipientType.CC, email.ccEmailAddresses)
        msg.replyTo = email.replyToEmailAddresses
        msg.setSubject(email.emailSubject)
        msg.setContent(email.emailBody, "text/html")

        // Add a configuration set header. Comment or delete the
        // next line if you are not using a configuration set
        if (emailSMTPConfig.CONFIGSET != null)
            msg.setHeader("X-SES-CONFIGURATION-SET", emailSMTPConfig.CONFIGSET)
        return msg
    }

    private fun saveMailInMongo(email: Email, emailStatus: EmailStatus): String? {
        var mongoEmail: com.und.model.mongo.Email = com.und.model.mongo.Email(
                email.clientID,
                email.fromEmailAddress,
                email.toEmailAddresses,
                email.ccEmailAddresses,
                email.bccEmailAddresses,
                email.replyToEmailAddresses,
                email.emailSubject!!,
                email.emailBody!!,
                email.emailTemplateId,
                email.eventUser?.id,
                emailStatus = emailStatus
        )
        TenantProvider().setTenant(email.clientID.toString())
        val s = emailSentRepository.save(mongoEmail)

        if(email.emailBody != null && s!=null && s.id != null)
            templateContentCreationService.trackAllURLs(email.emailBody!!, email.clientID, s.id!!)

        return s.id
    }

    fun updateEmailStatus(mongoEmailId: String, emailStatus: EmailStatus, clientId: Long, clickTrackEventId: String? = null) {
        TenantProvider().setTenant(clientId.toString())
        var mongoEmail: com.und.model.mongo.Email = emailSentRepository.findById(mongoEmailId).get()
        if (mongoEmail.emailStatus.order < emailStatus.order) {
            mongoEmail.emailStatus = EmailStatus.READ
            mongoEmail.statusUpdates.add(EmailStatusUpdate(LocalDateTime.now(), emailStatus, clickTrackEventId))
            emailSentRepository.save(mongoEmail)
        }
    }

    fun sendEmail(email: Email) {
        var emailTemplate: EmailTemplate? = null
        if (email.emailTemplateId != null) {
            emailTemplate = emailTemplateRepository.findByIdAndClientID(email.emailTemplateId!!, email.clientID)
            email.emailSubject = email.emailSubject ?: emailTemplate.emailTemplateSubject
            email.emailBody = email.emailBody ?: emailTemplate.emailTemplateBody
        }
        if (email.eventUser != null) {
            email.emailSubject = templateContentCreationService.getContentFromTemplate(email.emailTemplateId.toString(), emailTemplate!!.emailTemplateSubject, mapOf(Pair("user", email.eventUser!!)))
            email.emailBody = templateContentCreationService.getContentFromTemplate(email.emailTemplateId.toString(), emailTemplate.emailTemplateBody, mapOf(Pair("user", email.eventUser!!)))
        }
        getCredentialsAndSendEmail(email)
    }

    private fun getCredentialsAndSendEmail(email: Email) {
        synchronized(email.clientID) {
            //TODO: This code can be cached in Redis
            if (!wspCredsMap.containsKey(email.clientID)) {
                val serviceProviderCreds = serviceProviderCredentialsRepository.findByClientIDAndServiceProviderTypeAndStatus(email.clientID, "Email Service Provider", Status.ACTIVE).first()
                val wspCreds = serviceProviderCredentialsService.buildWebServiceProviderCredentials(serviceProviderCreds)
                wspCredsMap.put(email.clientID, wspCreds)
            }
        }
        val wspCreds = wspCredsMap[email.clientID]!!
        when (wspCreds.serviceProvider) {
            "SMTP" -> {
                val emailSMTPConfig = EmailSMTPConfig.build(wspCreds)
                sendEmailBySMTP(emailSMTPConfig, email)
            }
            "AWS - Simple Email Service (API)" -> {
                val emailSESConfig = EmailSESConfig.build(wspCreds)
                sendEmailByAWSSDK(emailSESConfig, email)
            }
            "AWS - Simple Email Service (SMTP)" -> {
                val emailSMTPConfig = com.und.model.utils.EmailSMTPConfig.build(wspCreds)
                sendEmailBySMTP(emailSMTPConfig, email)
            }
        }
    }
}
