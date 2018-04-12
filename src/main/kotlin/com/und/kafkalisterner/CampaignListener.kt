package com.und.kafkalisterner

import com.und.model.utils.Email
import com.und.service.CampaignService
import com.und.utils.loggerFor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.stereotype.Service

@Service
class CampaignListener {

    @Autowired
    private lateinit var campaignService: CampaignService

    companion object {
        val logger = loggerFor(CampaignListener::class.java)
    }

    @StreamListener("emailEvent")
    fun executeCampaign(campaignMap: Map<String,Long>) {
        campaignService.executeCampaign(campaignMap["campaignMap"]!!, campaignMap["clientId"]!!)
    }
}