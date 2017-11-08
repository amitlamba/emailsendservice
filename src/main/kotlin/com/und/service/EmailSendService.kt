package com.und.service

import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.Body
import com.amazonaws.services.simpleemail.model.Content
import com.amazonaws.services.simpleemail.model.Destination
import com.amazonaws.services.simpleemail.model.SendEmailRequest
import com.und.factory.EmailServiceProviderConnectionFactory
import com.und.model.Email
import com.und.model.EmailRead
import com.und.model.mongo.EmailStatus
import com.und.repository.EmailSentRepository
import com.und.repository.ServiceProviderCredentialsRepository
import com.und.utils.TenantProvider
import com.und.utils.loggerFor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.MimeMessage
import com.amazonaws.services.simpleemail.model.Message as SESMessage


@Service
class EmailSendService {
    companion object {
        protected val logger = loggerFor(javaClass)
    }

    @Autowired
    lateinit private var serviceProviderCredentialsRepository: ServiceProviderCredentialsRepository

    @Autowired
    lateinit private var emailSentRepository: EmailSentRepository

    @Autowired
    lateinit private var emailServiceProviderConnectionFactory: EmailServiceProviderConnectionFactory

    fun sendEmailByAWSSDK(emailSESConfig: EmailSESConfig, email: Email) {
        try {
            val client = AmazonSimpleEmailServiceClientBuilder.standard()
                    // Replace US_WEST_2 with the AWS Region you're using for
                    // Amazon SES.
                    .withRegion(Regions.EU_WEST_2).build()
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

                configurationSetName = emailSESConfig.CONFIGSET
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


            val msg = createMimeMessage(session, email, emailSMTPConfig!!)
            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients())
            transport.sendMessage(msg, msg.getAllRecipients())
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

    private fun createSMTPSession(emailSMTPConfig: EmailSMTPConfig): Session {
        // Create a Properties object to contain connection configuration information.
        val props = System.getProperties()
        props.put("mail.transport.protocol", "smtp")
        props.put("mail.smtp.port", emailSMTPConfig.PORT)
        props.put("mail.smtp.starttls.enable", "true")
        props.put("mail.smtp.auth", "true")

        // Create a Session object to represent a mail session with the specified properties.
        val session = Session.getDefaultInstance(props)
        return session
    }

    private fun saveMailInMongo(email: Email, emailStatus: EmailStatus) {
        var mongoEmail: com.und.model.mongo.Email = com.und.model.mongo.Email(
                email.clientID,
                email.fromEmailAddress,
                email.toEmailAddresses,
                email.ccEmailAddresses,
                email.bccEmailAddresses,
                email.replyToEmailAddresses,
                email.emailSubject,
                email.emailBody,
                email.userID,
                emailStatus = emailStatus
        )
        TenantProvider().setTenant(email.clientID.toString())
        emailSentRepository.save(mongoEmail)
    }

    fun markEmailRead(emailRead: EmailRead) {
        val clientID = emailRead.clientID
        TenantProvider().setTenant(clientID.toString())
        var mongoEmail: com.und.model.mongo.Email = emailSentRepository.findById(emailRead.emailUid).get()
        if (mongoEmail.emailStatus == EmailStatus.SENT) {
            mongoEmail.emailStatus = EmailStatus.READ
            emailSentRepository.save(mongoEmail)
        }
    }
}

data class EmailSESConfig(
        var clientID: Long,
        var CONFIGSET: String? = "CONFIGSET"
)

data class EmailSMTPConfig(
        var clientID: Long,
        var HOST: String,
        var PORT: Int,
        var SMTP_USERNAME: String,
        var SMTP_PASSWORD: String,
        var CONFIGSET: String? = null
)