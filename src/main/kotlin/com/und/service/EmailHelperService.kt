package com.und.service

import com.und.factory.EmailServiceProviderConnectionFactory
import com.und.model.mongo.EmailStatus
import com.und.model.mongo.EmailStatusUpdate
import com.und.model.utils.Email
import com.und.model.utils.EmailSMTPConfig
import com.und.model.utils.ServiceProviderCredentials
import com.und.repository.EmailSentRepository
import com.und.utils.TenantProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.MimeMessage

@Service
class EmailHelperService {

    @Autowired
    private lateinit var emailSentRepository: EmailSentRepository

    @Autowired
    private lateinit var templateContentCreationService: TemplateContentCreationService

    @Autowired
    private lateinit var emailServiceProviderConnectionFactory: EmailServiceProviderConnectionFactory

    fun createMimeMessage(session: Session, email: Email): MimeMessage {
        val emailSMTPConfig = emailServiceProviderConnectionFactory.getEmailServiceProvider(email.clientID)
        val msg = MimeMessage(session)
        msg.setFrom(email.fromEmailAddress)
        msg.setRecipients(Message.RecipientType.TO, email.toEmailAddresses)
        msg.setRecipients(Message.RecipientType.CC, email.ccEmailAddresses)
        msg.replyTo = email.replyToEmailAddresses
        msg.subject = email.emailSubject
        msg.setContent(email.emailBody, "text/html")
        if (emailSMTPConfig.CONFIGSET != null)
            msg.setHeader("X-SES-CONFIGURATION-SET", emailSMTPConfig.CONFIGSET)
        return msg
    }

    fun saveMailInMongo(email: Email, emailStatus: EmailStatus): String? {
        val mongoEmail: com.und.model.mongo.Email = com.und.model.mongo.Email(
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
        val mmongoEmailPersisted: com.und.model.mongo.Email? = emailSentRepository.save(mongoEmail)

        return mmongoEmailPersisted?.let {
            val id = mmongoEmailPersisted.id
            val emailBody = mmongoEmailPersisted.emailBody
            val clientId = email.clientID
            if(id!=null) {
                templateContentCreationService.trackAllURLs(emailBody, clientId, id)
            }
            return id
        }

    }

    fun updateEmailStatus(mongoEmailId: String, emailStatus: EmailStatus, clientId: Long, clickTrackEventId: String? = null) {
        TenantProvider().setTenant(clientId.toString())
        val mongoEmail: com.und.model.mongo.Email = emailSentRepository.findById(mongoEmailId).get()
        if (mongoEmail.emailStatus.order < emailStatus.order) {
            mongoEmail.emailStatus = EmailStatus.READ
            mongoEmail.statusUpdates.add(EmailStatusUpdate(LocalDateTime.now(), emailStatus, clickTrackEventId))
            emailSentRepository.save(mongoEmail)
        }
    }

    fun updateSubjectAndBody(email: Email): Email {
        val emailToSend = email.copy()
        val model = emailToSend.data
        emailToSend.eventUser?.let {
            model["user"] = it
        }
        emailToSend.emailSubject = templateContentCreationService.getEmailSubject(emailToSend, model)
        emailToSend.emailBody = templateContentCreationService.getEmailBody(emailToSend, model)
        return emailToSend
    }

    fun session(clientId:Long, emailSMTPConfig:EmailSMTPConfig) = emailServiceProviderConnectionFactory.getSMTPSession(clientId, emailSMTPConfig)

    fun transport(clientId:Long) = emailServiceProviderConnectionFactory.getSMTPTransportConnection(clientId)

    fun closeTransport(clientId:Long) = emailServiceProviderConnectionFactory.closeSMTPTransportConnection(clientId)


}