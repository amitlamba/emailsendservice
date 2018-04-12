package com.und.repository

import com.und.model.jpa.Campaign
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CampaignRepository: JpaRepository<Campaign, Long> {

    @Query("select\n" +
            "  c.id as campaignId,\n" +
            "  c.segmentation_id as segmentId,\n" +
            "  c.campaign_type as campaignType,\n" +
            "  ec.email_template_id as emailTemplateId,\n" +
            "  sc.sms_template_id as smsTemplateId,\n" +
            "  et.from_user as fromEmailAddress,\n" +
            "  st.from_user as fromSMSUser,\n" +
            "  c.client_id as clientId\n" +
            "from campaign c\n" +
            "  LEFT JOIN email_campaign ec on c.id = ec.campaign_id and ec.client_id = c.client_id\n" +
            "  LEFT JOIN sms_campaign sc on c.id = sc.campaign_id and sc.client_id = c.client_id\n" +
            "  LEFT JOIN email_template et on et.id = ec.email_template_id and et.client_id = c.client_id\n" +
            "  LEFT JOIN sms_template st on st.id = sc.sms_template_id and st.client_id = c.client_id\n" +
            "where c.id = :campaignId and (c.campaign_status <> ‘deleted’ or c.campaign_status is null) and c.client_id = :clientId;",
            nativeQuery = true)
    fun getCampaignByCampaignId(campaignId: Long, clientId: Long): Campaign?
}