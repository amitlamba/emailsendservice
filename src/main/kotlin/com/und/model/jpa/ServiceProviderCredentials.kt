package com.und.model.jpa

import com.amazonaws.regions.Regions
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "service_provider_credentials")
class ServiceProviderCredentials {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "service_provider_credentials_id_seq")
    @SequenceGenerator(name = "service_provider_credentials_id_seq", sequenceName = "service_provider_credentials_id_seq", allocationSize = 1)
    var id: Long? = null

    @Column(name = "client_id")
    @NotNull
    var clientID: Long? = null

    @Column(name = "appuser_id")
    @NotNull
    var appuserID: Long? = null

    @Column(name = "service_provider_type")
    @NotNull
    lateinit var serviceProviderType: String

    @Column(name = "service_provider")
    @NotNull
    lateinit var serviceProvider: String

    //    @Transient
// FIXME: This date should not be modified on subsequent changes
    @Column(name = "date_created")
    @NotNull
    lateinit var dateCreated: LocalDateTime

    @Column(name = "date_modified")
    @NotNull
    lateinit var dateModified: LocalDateTime

    @Column(name = "status")
    @NotNull
    @Enumerated(EnumType.STRING)
    lateinit var status: Status

    @Column(name = "credentials")
    @NotNull
    lateinit var credentialsMap: String
}

enum class ServiceProvider(val type: ServiceProviderType) {
    SMTP(ServiceProviderType.EMAIL_SERVICE_PROVIDER),
    AWS_SES(ServiceProviderType.EMAIL_SERVICE_PROVIDER),
    AWS_SNS(ServiceProviderType.SMS_SERVICE_PROVIDER),
    GOOGLE_FCM(ServiceProviderType.NOTIFICATIONS_SERVICE_PROVIDER);

    companion object {
        private val map:Map<ServiceProviderType, List<ServiceProvider>> = ServiceProvider.values().groupBy(ServiceProvider::type)
        fun getProviders(type: ServiceProviderType) = map[type]
    }
}

enum class ServiceProviderType(val value: Short) {
    EMAIL_SERVICE_PROVIDER(1),
    SMS_SERVICE_PROVIDER(2),
    NOTIFICATIONS_SERVICE_PROVIDER(3);

    companion object {
        private val map = ServiceProviderType.values().associateBy(ServiceProviderType::value)
        fun fromValue(type: Short) = map[type]
    }
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
)

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