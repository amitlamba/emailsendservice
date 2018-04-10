package com.und.model.utils

import com.amazonaws.regions.Regions
import com.und.model.jpa.Status
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.HashMap

class ServiceProviderCredentials {
    var id: Long? = null
    var clientID: Long? = null
    var appuserID: Long? = null
    lateinit var serviceProviderType: String
    lateinit var serviceProvider: String
    lateinit var dateCreated: LocalDateTime
    lateinit var dateModified: LocalDateTime
    lateinit var status: Status
    var credentialsMap: HashMap<String, String> = HashMap<String, String>()
}

class ServiceProviderCreds {
    lateinit var serviceProviders: Map<String, ServiceProviderTypes>
}

class ServiceProviderTypes {
    lateinit var name: String
    lateinit var displayName: String
    lateinit var providers: Map<String, ServiceProvider>
}

class ServiceProvider {
    lateinit var name: String
    lateinit var displayName: String
    lateinit var fields: Array<Field>
}

class Field {
    lateinit var fieldName: String
    lateinit var fieldDisplayName: String
    var required: Boolean = false
    lateinit var fieldType: String
}

data class EmailSESConfig(
        var serviceProviderCredentialsId: Long?,
        val clientID: Long,
        val CONFIGSET: String? = "CONFIGSET",
        val region: Regions,
        val awsAccessKeyId: String,
        val awsSecretAccessKey: String
)

data class EmailSMTPConfig(
        var serviceProviderCredentialsId: Long?,
        var clientID: Long,
        var HOST: String,
        var PORT: Int,
        var SMTP_USERNAME: String,
        var SMTP_PASSWORD: String,
        var CONFIGSET: String? = null
) {
    companion object {
        fun build(serviceProviderCredentials: ServiceProviderCredentials): EmailSMTPConfig {
            val host = serviceProviderCredentials.credentialsMap.get("url")
            val port = serviceProviderCredentials.credentialsMap.get("port")
            val username = serviceProviderCredentials.credentialsMap.get("username")
            val password = serviceProviderCredentials.credentialsMap.get("password")
            return EmailSMTPConfig(
                    serviceProviderCredentials.id,
                    serviceProviderCredentials.clientID!!,
                    host!!,
                    port!!.toInt(),
                    username!!,
                    password!!
            )
        }
    }
}

data class SmsSNSConfig(
        var serviceProviderCredentialsId: Long?,
        val clientID: Long,
        val region: Regions,
        val awsAccessKeyId: String,
        val awsSecretAccessKey: String
)

data class GoogleFCMConfig(
        var serviceProviderCredentialsId: Long?,
        val clientID: Long,
        val serverKey: String
)

data class SP(val spType: String, val sp: String)

val mapSPtoSPClass: Map<SP, Any> = mapOf(SP("Email Service Provider", "SMTP") to EmailSMTPConfig.javaClass)