package com.und.service

import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.Body
import com.amazonaws.services.simpleemail.model.Content
import com.amazonaws.services.simpleemail.model.Destination
import com.amazonaws.services.simpleemail.model.SendEmailRequest
import com.und.model.Email
import org.springframework.stereotype.Service
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


@Service
class EmailSendService {

    fun sendEmailByAWSSDK(emailSESConfig: EmailSESConfig, email: Email) {
        try {
            val client = AmazonSimpleEmailServiceClientBuilder.standard()
                    // Replace US_WEST_2 with the AWS Region you're using for
                    // Amazon SES.
                    .withRegion(Regions.EU_WEST_2).build()
            val request = SendEmailRequest()
                    .withDestination(
                            Destination().withToAddresses(
                                    (addressArrayToStringList(email.toEmailAddresses))))
                    .withMessage(com.amazonaws.services.simpleemail.model.Message()
                            .withBody(Body()
                                    .withHtml(Content()
                                            .withCharset("UTF-8").withData(email.emailBody))
                                    .withText(Content()
                                            .withCharset("UTF-8").withData(email.emailBody)))
                            .withSubject(Content()
                                    .withCharset("UTF-8").withData(email.emailSubject)))
                    .withSource(email.fromEmailAddress.address)
                    // Comment or remove the next line if you are not using a
                    // configuration set
                    .withConfigurationSetName(emailSESConfig.CONFIGSET)
            client.sendEmail(request)
            println("Email sent!")
        } catch (ex: Exception) {
            println("The email was not sent. Error message: " + ex.message)
        }

    }

    private fun addressArrayToStringList(emailAddresses: Array<InternetAddress>): MutableList<String> {
        var stringAddresses = mutableListOf<String>()
        for (emailAddress in emailAddresses) {
            stringAddresses.add(emailAddress.address)
        }
        return stringAddresses
    }

    fun sendEmailBySMTP(emailSMTPConfig: EmailSMTPConfig, email: Email) {

        val session = createSMTPSession(emailSMTPConfig)
        val msg = createMimeMessage(session, email, emailSMTPConfig)


        // Create a transport.
        val transport = session.getTransport()

        // Send the message.
        try {
            println("Sending...")

            // Connect to Amazon SES using the SMTP username and password you specified above.
            transport.connect(emailSMTPConfig.HOST, emailSMTPConfig.SMTP_USERNAME, emailSMTPConfig.SMTP_PASSWORD)

            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients())
            transport.sendMessage(msg, msg.getAllRecipients())
            println("Email sent!")
        } catch (ex: Exception) {
            println("The email was not sent.")
            println("Error message: " + ex.message)
        } finally {
            // Close and terminate the connection.
            transport.close()
        }
    }

    private fun createMimeMessage(session: Session, email: Email, emailSMTPConfig: EmailSMTPConfig): MimeMessage {
        // Create a message with the specified information.
        val msg = MimeMessage(session)
        msg.setFrom(email.fromEmailAddress)
        msg.setRecipients(Message.RecipientType.TO, email.toEmailAddresses)
        msg.setRecipients(Message.RecipientType.CC, email.ccEmailAddresses)
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
}

class EmailSESConfig(
        var CONFIGSET: String? = "CONFIGSET"
)

class EmailSMTPConfig(
        var HOST: String,
        var PORT: Int,
        var SMTP_USERNAME: String,
        var SMTP_PASSWORD: String,
        var CONFIGSET: String? = null
)