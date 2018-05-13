package com.und.kafkalisterner

import com.und.model.utils.Email
import com.und.model.utils.EmailUpdate
import com.und.service.ClickTrackService
import com.und.service.EmailSendService
import com.und.utils.loggerFor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.stereotype.Service

@Service
class EmailListener {


    @Autowired
    private lateinit var emailService: EmailSendService

    @Autowired
    private lateinit var clickTrackService: ClickTrackService

    companion object {
        val logger = loggerFor(EmailListener::class.java)
    }




    @StreamListener("emailEventSend")
    fun sendEmaiCampaignl(email: Email) {
        emailService.sendEmail(email)
    }

    @StreamListener("clientEmailReceive")
    fun sendClientEmail(email: Email) {
        email.clientID = 1
        emailService.sendEmailBySMTP(null, email)
    }

    @StreamListener("EmailUpdateReceive")
    fun listenEmailUpdate(emailUpdate: EmailUpdate) {
        try {
            emailService.updateEmailStatus(emailUpdate.mongoEmailId, emailUpdate.emailStatus, emailUpdate.clientID, emailUpdate.eventId)
        } catch (ex: Exception) {
            logger.error("Error while Updating Email $emailUpdate", ex.message)
        }
    }

}
