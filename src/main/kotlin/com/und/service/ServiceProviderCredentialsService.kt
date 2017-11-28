package com.und.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.und.model.*
import com.und.repository.ServiceProviderCredentialsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ServiceProviderCredentialsService {

    @Autowired
    private lateinit var serviceProviderCredentialsRepository: ServiceProviderCredentialsRepository

    fun getEmailProviderCredentials(clientId: Long): Any {
        val serviceProvider = serviceProviderCredentialsRepository.findByClientIDAndServiceProviderTypeAndStatus(clientId, ServiceProviderType.EMAIL_SERVICE_PROVIDER, Status.ACTIVE).single()
        when(serviceProvider.serviceProvider) {
            ServiceProvider.AWS_SES -> return jacksonObjectMapper().readValue<EmailSESConfig>(serviceProvider.credentials)
            ServiceProvider.SMTP -> return jacksonObjectMapper().readValue<EmailSMTPConfig>(serviceProvider.credentials)
            else -> throw Exception("No Email Provider for Client with ID ${clientId}")
        }
    }

    fun getSmsProviderCredentials(clientId: Long): Any {
        val serviceProvider = serviceProviderCredentialsRepository.findByClientIDAndServiceProviderTypeAndStatus(clientId, ServiceProviderType.SMS_SERVICE_PROVIDER, Status.ACTIVE).single()
        when(serviceProvider.serviceProvider) {
            ServiceProvider.AWS_SNS -> return jacksonObjectMapper().readValue<SmsSNSConfig>(serviceProvider.credentials)
            else -> throw Exception("No Sms Provider for Client with ID ${clientId}")
        }
    }

    fun getNotificationProviderCredentials(clientId: Long): Any {
        val serviceProvider = serviceProviderCredentialsRepository.findByClientIDAndServiceProviderTypeAndStatus(clientId, ServiceProviderType.NOTIFICATIONS_SERVICE_PROVIDER, Status.ACTIVE).single()
        when(serviceProvider.serviceProvider) {
            ServiceProvider.GOOGLE_FCM -> return jacksonObjectMapper().readValue<GoogleFCMConfig>(serviceProvider.credentials)
            else -> throw Exception("No Notification Provider for Client with ID ${clientId}")
        }
    }

    fun saveSMTPEmailProviderCredentials(clientId: Long, appUserId: Long, emailSMTPConfig: EmailSMTPConfig): EmailSMTPConfig {
        var serviceProviderCredentials: ServiceProviderCredentials = ServiceProviderCredentials()
        serviceProviderCredentials.serviceProvider=ServiceProvider.SMTP
        serviceProviderCredentials.serviceProviderType=ServiceProviderType.EMAIL_SERVICE_PROVIDER
        serviceProviderCredentials.clientID=clientId
        serviceProviderCredentials.appuserID=appUserId
        serviceProviderCredentials.credentials= jacksonObjectMapper().writeValueAsString(emailSMTPConfig)
        serviceProviderCredentials.status=Status.ACTIVE
        emailSMTPConfig.serviceProviderCredentialsId = saveServiceProviderCredentials(serviceProviderCredentials)
        return emailSMTPConfig
    }

    fun saveSESEmailProviderCredentials(clientId: Long, appUserId: Long, emailSESConfig: EmailSESConfig): EmailSESConfig {
        var serviceProviderCredentials: ServiceProviderCredentials = ServiceProviderCredentials()
        serviceProviderCredentials.serviceProvider=ServiceProvider.AWS_SES
        serviceProviderCredentials.serviceProviderType=ServiceProviderType.EMAIL_SERVICE_PROVIDER
        serviceProviderCredentials.clientID=clientId
        serviceProviderCredentials.appuserID=appUserId
        serviceProviderCredentials.credentials= jacksonObjectMapper().writeValueAsString(emailSESConfig)
        serviceProviderCredentials.status=Status.ACTIVE
        emailSESConfig.serviceProviderCredentialsId = saveServiceProviderCredentials(serviceProviderCredentials)
        return emailSESConfig
    }

    fun saveSnsSmsProviderCredentials(clientId: Long, appUserId: Long, emailSESConfig: EmailSESConfig): EmailSESConfig {
        var serviceProviderCredentials: ServiceProviderCredentials = ServiceProviderCredentials()
        serviceProviderCredentials.serviceProvider=ServiceProvider.AWS_SNS
        serviceProviderCredentials.serviceProviderType=ServiceProviderType.SMS_SERVICE_PROVIDER
        serviceProviderCredentials.clientID=clientId
        serviceProviderCredentials.appuserID=appUserId
        serviceProviderCredentials.credentials= jacksonObjectMapper().writeValueAsString(emailSESConfig)
        serviceProviderCredentials.status=Status.ACTIVE
        emailSESConfig.serviceProviderCredentialsId = saveServiceProviderCredentials(serviceProviderCredentials)
        return emailSESConfig
    }

    private fun saveServiceProviderCredentials(serviceProviderCredentials: ServiceProviderCredentials): Long {
        val saved = serviceProviderCredentialsRepository.save(serviceProviderCredentials)
        return saved.id!!
    }
}