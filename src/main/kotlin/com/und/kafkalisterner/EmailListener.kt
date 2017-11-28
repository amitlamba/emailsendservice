package com.und.kafkalisterner

import com.und.model.*
import com.und.model.eventapi.ClickTrackEvent
import com.und.repository.ServiceProviderCredentialsRepository
import com.und.service.ClickTrackService
import com.und.service.EmailSendService
import com.und.utils.loggerFor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.TopicPartition
import org.springframework.messaging.handler.annotation.Payload

class EmailListener {

    //TODO externalize topic partition names

    @Value("\${kafka.ip}")
    lateinit private var ip: String

    @Value("\${kafka.topic.email}")
    lateinit private var topic: String

    private val topicEmailRead: String = "EmailRead"

    @Autowired
    lateinit private var emailService: EmailSendService

    @Autowired
    private lateinit var clickTrackService: ClickTrackService

    companion object {
        val logger = loggerFor(EmailListener::class.java)
    }

    /*
    , @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) key:Int,
                             @Header(KafkaHeaders.RECEIVED_PARTITION_ID) partition :Int,
                             @Header(KafkaHeaders.RECEIVED_TOPIC) topic :String,
                             @Header(KafkaHeaders.OFFSET) offset : Long
     */
    @KafkaListener(id = "id0", topicPartitions = arrayOf(TopicPartition(topic = "Email", partitions = arrayOf("0"))), containerFactory = "kafkaListenerContainerFactory")
    fun listenPartition0(@Payload email: Email) {
        sendEmail(email)
    }

    @KafkaListener(id = "id1", topicPartitions = arrayOf(TopicPartition(topic = "Email", partitions = arrayOf("1"))), containerFactory = "kafkaListenerContainerFactory")
    fun listenPartition1(email: Email) {
        sendEmail(email)
    }

    @KafkaListener(id = "id2", topicPartitions = arrayOf(TopicPartition(topic = "Email", partitions = arrayOf("2"))), containerFactory = "kafkaListenerContainerFactory")
    fun listenPartition2(email: Email) {
        sendEmail(email)
    }

    @KafkaListener(id = "emailReadId0", topicPartitions = arrayOf(TopicPartition(topic = "EmailRead", partitions = arrayOf("1"))), containerFactory = "kafkaListenerContainerFactory")
    fun listenEmailReadTopicAllPartitions(emailRead: EmailRead) {
        try {
            emailService.markEmailRead(emailRead)
        } catch (ex: Exception) {
            logger.error("Error while Marking Email ${emailRead} Read", ex.message)
        }
    }

    @KafkaListener(id = "clickTrack", topicPartitions = arrayOf(TopicPartition(topic = "ClickTrack", partitions = arrayOf("1"))), containerFactory = "kafkaListenerContainerFactory")
    fun listenClickTrackTopicAllPartitions(clickTrackEvent: ClickTrackEvent) {
        try {
            clickTrackService.markClickTrack(clickTrackEvent)
        } catch (ex: Exception) {
            logger.error("Error while Marking Click Track ${clickTrackEvent} Event", ex.message)
        }
    }

    private fun sendEmail(email: Email) {
        //TODO handle setting client id in Event bean set client id in eventUser as well
//        emailService.sendEmailBySMTP(emailSMTPConfig = createEmailSMTPConfigForClient(email.clientID), email = email)
        emailService.sendEmailBySMTP(null, email)
    }

    private fun createEmailSMTPConfigForClient(clientID: Long): EmailSMTPConfig {
        val SMTP_USERNAME = "smtp_username"
        val SMTP_PASSWORD = "smtp_password"
        val CONFIGSET = "ConfigSet"
        val HOST = "email-smtp.us-west-2.amazonaws.com"
        val PORT = 587
        return EmailSMTPConfig(null, clientID, HOST, PORT, SMTP_USERNAME, SMTP_PASSWORD, CONFIGSET)
    }
}
