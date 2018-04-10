package com.und.model.mongo

import com.und.model.jpa.ServiceProvider
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*
import javax.mail.internet.InternetAddress

@Document(collection = "#{tenantProvider.getTenant()}_email")
data class Email(
        var clientID: Long,
        var fromEmailAddress: InternetAddress,
        var toEmailAddresses: Array<InternetAddress>,
        var ccEmailAddresses: Array<InternetAddress>? = null,
        var bccEmailAddresses: Array<InternetAddress>? = null,
        var replyToEmailAddresses: Array<InternetAddress>? = null,
        var emailSubject: String,
        @Transient
        var emailBody: String,
        var emailTemplateId: Long? = null,
        var userID: String? = null,
        @Id
        var id: String? = null, //Mongo Auto-generated Document id
        var emailProviderMessageID: String? = null,
        var emailServiceProvider: ServiceProvider? = null,
        var emailStatus: EmailStatus,
        var statusUpdates: MutableList<EmailStatusUpdate> = mutableListOf()
)

data class EmailStatusUpdate (
        val date: Date,
        val emailStatus: EmailStatus,
        val clickTrackEventId: String? = null
)

enum class EmailStatus {
    NOT_SENT,
    SENT,
    READ,
    CTA_PERFORMED
}