package com.und.service

import com.und.config.EventStream
import com.und.model.mongo.EventUser
import com.und.model.utils.Email
import com.und.repository.CampaignRepository
import com.und.utils.loggerFor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import javax.mail.internet.InternetAddress

@Service
class CampaignService {

    companion object {
        protected val logger = loggerFor(CampaignService::class.java)
    }

    @Autowired
    private lateinit var campaignRepository: CampaignRepository

    @Autowired
    private lateinit var segmentService: SegmentService

    @Autowired
    private lateinit var eventStream: EventStream

    fun executeCampaign(campaignId: Long, clientId: Long) {
        val campaign = campaignRepository.getCampaignByCampaignId(campaignId, clientId)
        val usersData = getUsersData(campaign?.segmentId ?: 0, clientId)
        usersData.forEach {
            try {
                var email: Email = Email(
                        clientId,
                        InternetAddress.parse(campaign?.fromEmailAddress, false)[0],
                        InternetAddress.parse(it.identity.email, false),
                        null,
                        null,
                        null,
                        null,
                        null,
                        campaign?.emailTemplateId
                )
                toKafka(email)
            } catch (ex: Exception) {
                logger.error(ex.message)
                ex.printStackTrace()
                //TODO: Catch the exception
            } finally {
                //TODO: Handle Finally clause
            }
        }
    }

    fun getUsersData(segmentId: Long, clientId: Long): List<EventUser> {
        val segment = segmentService.getWebSegment(segmentId, clientId)
        val userData: List<EventUser> = segmentService.getUserData(segment)
        return userData
    }

    fun toKafka(email: Email): Boolean =
            eventStream.outputEmailEvent().send(MessageBuilder.withPayload(email).build())
}