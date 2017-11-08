package com.und.eventapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.mobile.device.Device

data class SystemDetails(
        var OS: String = "",
        var browser: String = "",
        var browserVersion: String = "",
        var deviceType: String = "", //mobile, tablet, laptop etc
        var appId: String = "",
        var agentString:String = ""

)