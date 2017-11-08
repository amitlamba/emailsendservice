package com.und.eventapi.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "#{tenantProvider.getTenant()}_eventUser")
data class EventUser(
        @Id
        var id: String? = null,
        var isIdentified: Boolean = false,

        var tempId: String? = "",
        var instanceId: String = "", //this is used when a new cookie value for und is found

        var undUserId: String = "", //this is permanent id of user , in und db
        var clientUserId: String = "", //this is id that client system identifies user with
        var clientId: String = "", //id of the client in our system
        var deviceId: ArrayList<String> = arrayListOf(),
        var fbId: String = "",
        var googleId: String = "",
        var mobile: String = "",
        var email: String = "",
        var firstname: String = "",
        var lastname: String = "",
        val localDateTime: Long = System.currentTimeMillis()
        //TODO - By Amit Lamba - We should track country code
//TODO - By Amit Lamba - We should track custom user attributes
)