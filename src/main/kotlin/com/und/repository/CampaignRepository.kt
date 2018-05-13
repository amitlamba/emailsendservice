package com.und.repository

import com.und.model.jpa.Campaign
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CampaignRepository: JpaRepository<Campaign, Long> {

    @Query("""select
                      c.id as campaignId,
                      c.segmentation_id as segmentId,
                      c.campaign_type as campaignType,
                      ec.email_template_id as emailTemplateId,
                      sc.sms_template_id as smsTemplateId,
                      et.from_user as fromEmailAddress,
                      st.from_user as fromSMSUser,
                      c.client_id as clientId
                    from campaign c
                      LEFT JOIN email_campaign ec on c.id = ec.campaign_id and ec.client_id = c.client_id
                      LEFT JOIN sms_campaign sc on c.id = sc.campaign_id and sc.client_id = c.client_id
                      LEFT JOIN email_template et on et.id = ec.email_template_id and et.client_id = c.client_id
                      LEFT JOIN sms_template st on st.id = sc.sms_template_id and st.client_id = c.client_id
                    where c.id = :campaignId and (c.campaign_status <> 'deleted' or c.campaign_status is null) and c.client_id = :clientId""",
            nativeQuery = true)
    fun getCampaignByCampaignId(campaignId: Long, clientId: Long): Campaign?
}