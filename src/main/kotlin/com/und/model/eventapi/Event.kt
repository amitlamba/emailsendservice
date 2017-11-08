package com.und.eventapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

/**
 * Created by shiv on 21/07/17.
 */

@Document(collection = "#{tenantProvider.getTenant()}_event")
data class Event(
        val name: String = "",
        @Id
        private var id: String? = null,
        var clientId: String = "-1",
        var instanceId: String? = null,
        val eventUser: EventUser = EventUser(),
        val geoDetails: GeoDetails = GeoDetails(),
        val systemDetails: SystemDetails = SystemDetails(),
        val localDateTime: Long = System.currentTimeMillis(),
        val attributes: HashMap<String, Any> = hashMapOf(),
        var userIdentified:Boolean = false
)

