package com.und.kafkalisterner

import com.und.model.utils.Email
import com.und.model.utils.EmailRead
import com.und.model.mongo.ClickTrackEvent
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


    @StreamListener("emailEvent")
    fun sendEmail(email: Email) {
        emailService.sendEmailBySMTP(null, email)
    }

    @StreamListener("clientEmail")
    fun sendClientEmail(email: Email) {
        email.clientID = 1
        emailService.sendEmailBySMTP(null, email)
    }

    @StreamListener("EmailReadEvent")
    fun listenEmailReadTopicAllPartitions(emailRead: EmailRead) {
        try {
            emailService.markEmailRead(emailRead)
        } catch (ex: Exception) {
            logger.error("Error while Marking Email $emailRead Read", ex.message)
        }
    }

    @StreamListener("clickTrackEvent")
    fun listenClickTrackTopicAllPartitions(clickTrackEvent: ClickTrackEvent) {
        try {
            clickTrackService.markClickTrack(clickTrackEvent)
        } catch (ex: Exception) {
            logger.error("Error while Marking Click Track $clickTrackEvent Event", ex.message)
        }
    }



}
