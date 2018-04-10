package com.und.model.utils

import javax.mail.internet.InternetAddress

data class Email(
        var clientID: Long,
        var fromEmailAddress: InternetAddress,
        var toEmailAddresses: Array<InternetAddress>,
        var ccEmailAddresses: Array<InternetAddress>? = null,
        var bccEmailAddresses: Array<InternetAddress>? = null,
        var replyToEmailAddresses: Array<InternetAddress>? = null,
        var emailSubject: String? = null,
        var emailBody: String? = null,
        var emailTemplateId: Long? = null,
        var userID: String? = null
)