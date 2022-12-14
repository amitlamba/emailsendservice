package com.und.service

import com.und.model.mongo.ClickTrackEvent
import com.und.model.mongo.EmailStatus
import com.und.model.mongo.EmailStatusUpdate
import com.und.repository.ClickTrackEventRepository
import com.und.repository.EmailSentRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class ClickTrackService {
    private lateinit var clickTrackEventRepository: ClickTrackEventRepository
    private lateinit var emailSentRepository: EmailSentRepository

    fun markClickTrack(clickTrackEvent: ClickTrackEvent): ClickTrackEvent {
        val saved = clickTrackEventRepository.save(clickTrackEvent)
        val clickTrackEventId = saved.id
        if (clickTrackEvent.emailUid.isNotBlank()) {
            var email = emailSentRepository.findById(clickTrackEvent.emailUid).get()
            when (email.emailStatus) {
                EmailStatus.SENT, EmailStatus.READ -> {
                    email.statusUpdates.add(EmailStatusUpdate(LocalDateTime.now(), EmailStatus.CTA_PERFORMED, clickTrackEventId))
                    email.emailStatus = EmailStatus.CTA_PERFORMED
                    emailSentRepository.save(email)
                }
                EmailStatus.CTA_PERFORMED -> {}
                EmailStatus.NOT_SENT -> {}
            }
        }
        return clickTrackEvent
    }
}