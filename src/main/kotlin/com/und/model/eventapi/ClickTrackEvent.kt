package com.und.model.eventapi

import com.und.eventapi.model.EventUser
import com.und.eventapi.model.GeoDetails
import com.und.eventapi.model.SystemDetails
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.HashMap

@Document(collection = "#{tenantProvider.getTenant()}_click_event")
data class ClickTrackEvent(
        val name: String = "",
        @Id
        var id: String? = null,
        var clientId: String = "-1",
        var instanceId: String? = null,
        val eventUser: EventUser = EventUser(),
        val geoDetails: GeoDetails = GeoDetails(),
        val systemDetails: SystemDetails = SystemDetails(),
        val localDateTime: Long = System.currentTimeMillis(),
        val attributes: HashMap<String, Any> = hashMapOf(),
        var userIdentified:Boolean = false,
        var url: String,
        var emailUid: String
)