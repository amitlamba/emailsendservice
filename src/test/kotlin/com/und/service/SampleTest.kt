package com.und.service

import com.und.model.Email
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

//http://websystique.com/spring/spring-4-email-using-velocity-freemaker-template-library/

@RunWith(SpringRunner::class)
@SpringBootTest
class SampleTest {
    // Replace sender@example.com with your "From" address.
// This address must be verified.
    val FROM = "sender@example.com"
    val FROMNAME = "Sender Name"

    // Replace recipient@example.com with a "To" address. If your account
// is still in the sandbox, this address must be verified.
    val TO = "recipient@example.com"

    // Replace smtp_username with your Amazon SES SMTP user name.
    val SMTP_USERNAME = "smtp_username"

    // Replace smtp_password with your Amazon SES SMTP password.
    val SMTP_PASSWORD = "smtp_password"

    // The name of the Configuration Set to use for this message.
// If you comment out or remove this variable, you will also need to
// comment out or remove the header below.
    val CONFIGSET = "ConfigSet"

    // Amazon SES SMTP host name. This example uses the US West (Oregon) Region.
    val HOST = "email-smtp.us-west-2.amazonaws.com"

    // The port you will connect to on the Amazon SES SMTP endpoint.
    val PORT = 587

    val SUBJECT = "Amazon SES test (SMTP interface accessed using Java)"

    val BODY = arrayOf(
            "<h1>Amazon SES SMTP Email Test</h1>",
            "<p>This email was sent with Amazon SES using the ",
            "<a href='https://github.com/javaee/javamail'>Javamail Package</a>",
            " for <a href='https://www.java.com'>Java</a>.").joinToString(
            System.getProperty("line.separator"))

    @Autowired
    private lateinit var emailSendService: EmailSendService

    @Test
    fun testEmailSend() {
        val email: Email = Email(clientID = 1,
                fromEmailAddress = InternetAddress(FROM, FROMNAME),
                toEmailAddresses = arrayOf(InternetAddress(TO)),
                emailSubject = SUBJECT,
                emailBody = BODY)
        val emailSMTPConfig = EmailSMTPConfig(1, HOST, PORT, SMTP_USERNAME, SMTP_PASSWORD, CONFIGSET)

        emailSendService.sendEmailBySMTP(emailSMTPConfig, email)
    }

}
