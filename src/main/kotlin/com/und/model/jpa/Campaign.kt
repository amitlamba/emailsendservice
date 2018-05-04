package com.und.model.jpa

import javax.persistence.*

@Entity
@Table(name = "campaign")
class Campaign {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "campaign_id_seq")
    @SequenceGenerator(name = "campaign_id_seq", sequenceName = "campaign_id_seq", allocationSize = 1)
    var campaignId: Long? = null
    var segmentId: Long? = null
    var campaignType: String? = null
    var emailTemplateId: Long? = null
    var smsTemplateId: Long? = null
    var fromEmailAddress: String? = null
    var fromSMSUser: String? = null
    var clientId: Long? = null
}