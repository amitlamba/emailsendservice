package com.und.model.jpa

class Campaign {
    var campaignId: Long? = null
    var segmentId: Long? = null
    var campaignType: String? = null
    var emailTemplateId: Long? = null
    var smsTemplateId: Long? = null
    var fromEmailAddress: String? = null
    var fromSMSUser: String? = null
    var clientId: Long? = null
}