package com.und.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.gson.GsonBuilder
import com.und.model.jpa.*
import com.und.model.utils.EmailSESConfig
import com.und.model.utils.EmailSMTPConfig
import com.und.model.utils.GoogleFCMConfig
import com.und.model.utils.SmsSNSConfig
import com.und.repository.ServiceProviderCredentialsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import com.und.model.utils.ServiceProviderCredentials as WebServiceProviderCredentials

@Service
class ServiceProviderCredentialsService {

    private val serviceProviderJson: String = """
        {
            "Email Service Provider": {
              "name": "EmailServiceProvider",
              "displayName": "Email Service Provider",
              "providers": {
                "SMTP": {
                  "name": "SMTP",
                  "displayName": "SMTP",
                  "fields": [
                    {
                      "fieldName": "url",
                      "fieldDisplayName": "URL",
                      "required": true,
                      "fieldType": "string",
                    },
                    {
                      "fieldName": "port",
                      "fieldDisplayName": "Port",
                      "required": true,
                      "fieldType": "number",
                    },
                    {
                      "fieldName": "username",
                      "fieldDisplayName": "Username",
                      "required": true,
                      "fieldType": "string",
                    },
                    {
                      "fieldName": "password",
                      "fieldDisplayName": "Password",
                      "required": true,
                      "fieldType": "string",
                    }
                  ]
                },
                "AWS - Simple Email Service (API)": {
                  "name": "AWS - Simple Email Service (API)",
                  "displayName": "AWS - Simple Email Service (API)",
                  "fields": [
                    {
                      "fieldName": "AWS_REGION",
                      "fieldDisplayName": "AWS Region",
                      "required": true,
                      "fieldType": "string",
                    },
                    {
                      "fieldName": "AWS_ACCESS_KEY_ID",
                      "fieldDisplayName": "AWS Access Key ID",
                      "required": true,
                      "fieldType": "string",
                    },
                    {
                      "fieldName": "AWS_SECRET_ACCESS_KEY",
                      "fieldDisplayName": "AWS Secret Access Key",
                      "required": true,
                      "fieldType": "string",
                    }
                  ]
                },
                "AWS - Simple Email Service (SMTP)": {
                  "name": "AWS - Simple Email Service (SMTP)",
                  "displayName": "AWS - Simple Email Service (SMTP)",
                  "fields": [
                    {
                      "fieldName": "url",
                      "fieldDisplayName": "URL",
                      "required": true,
                      "fieldType": "string",
                    },
                    {
                      "fieldName": "port",
                      "fieldDisplayName": "Port",
                      "required": true,
                      "fieldType": "number",
                    },
                    {
                      "fieldName": "username",
                      "fieldDisplayName": "Username",
                      "required": true,
                      "fieldType": "string",
                    },
                    {
                      "fieldName": "password",
                      "fieldDisplayName": "Password",
                      "required": true,
                      "fieldType": "string",
                    }
                  ]
                }
              }
            },
            "SMS Service Provider": {
              "name": "SMSServiceProvider",
              "displayName": "SMS Service Provider",
              "providers": {
                "AWS - Simple Email Service": {
                  "name": "AWS - Simple Email Service",
                  "displayName": "AWS - Simple Email Service",
                  "fields": [
                    {
                      "fieldName": "AWS_REGION",
                      "fieldDisplayName": "AWS Region",
                      "required": true,
                      "fieldType": "string",
                    },
                    {
                      "fieldName": "AWS_ACCESS_KEY_ID",
                      "fieldDisplayName": "AWS Access Key ID",
                      "required": true,
                      "fieldType": "string",
                    },
                    {
                      "fieldName": "AWS_SECRET_ACCESS_KEY",
                      "fieldDisplayName": "AWS Secret Access Key",
                      "required": true,
                      "fieldType": "string",
                    }
                  ]
                }
              }
            },
            "Notification Service Provider": {
              "name": "NotificationServiceProvider",
              "displayName": "Notification Service Provider",
              "providers": {
                "Google - FCM": {
                  "name": "Google - FCM",
                  "displayName": "Google - FCM",
                  "fields": [
                    {
                      "fieldName": "apiKey",
                      "fieldDisplayName": "API Key",
                      "required": true,
                      "fieldType": "string"
                    },
                    {
                      "fieldName": "senderId",
                      "fieldDisplayName": "Sender ID",
                      "required": true,
                      "fieldType": "string"
                    }
                  ]
                },
                "Google - GCM": {
                  "name": "Google - GCM"
                }
              }
            }
        }
      """

    @Autowired
    private lateinit var serviceProviderCredentialsRepository: ServiceProviderCredentialsRepository

    fun getEmailProviderCredentials(clientId: Long): Any {
        val serviceProvider = serviceProviderCredentialsRepository.findByClientIDAndServiceProviderTypeAndStatus(clientId, "Email Service Provider", Status.ACTIVE).single()
        when(serviceProvider.serviceProvider) {
            "AWS - Simple Email Service (SMTP)" -> return jacksonObjectMapper().readValue<EmailSESConfig>(serviceProvider.credentialsMap)
            "SMTP" -> return jacksonObjectMapper().readValue<EmailSMTPConfig>(serviceProvider.credentialsMap)
            else -> throw Exception("No Email Provider for Client with ID ${clientId}")
        }
    }

    fun getSmsProviderCredentials(clientId: Long): Any {
        val serviceProvider = serviceProviderCredentialsRepository.findByClientIDAndServiceProviderTypeAndStatus(clientId, "SMS Service Provider", Status.ACTIVE).single()
        when(serviceProvider.serviceProvider) {
            "AWS - Simple Notification Service" -> return jacksonObjectMapper().readValue<SmsSNSConfig>(serviceProvider.credentialsMap)
            else -> throw Exception("No Sms Provider for Client with ID ${clientId}")
        }
    }

    fun getNotificationProviderCredentials(clientId: Long): Any {
        val serviceProvider = serviceProviderCredentialsRepository.findByClientIDAndServiceProviderTypeAndStatus(clientId, "Notification Service Provider", Status.ACTIVE).single()
        when(serviceProvider.serviceProvider) {
            "Google - FCM" -> return jacksonObjectMapper().readValue<GoogleFCMConfig>(serviceProvider.credentialsMap)
            else -> throw Exception("No Notification Provider for Client with ID ${clientId}")
        }
    }

    /*fun saveSMTPEmailProviderCredentials(clientId: Long, appUserId: Long, emailSMTPConfig: EmailSMTPConfig): EmailSMTPConfig {
        var serviceProviderCredentials: ServiceProviderCredentials = ServiceProviderCredentials()
        serviceProviderCredentials.serviceProvider= "SMTP"
        serviceProviderCredentials.serviceProviderType= ServiceProviderType.EMAIL_SERVICE_PROVIDER
        serviceProviderCredentials.clientID=clientId
        serviceProviderCredentials.appuserID=appUserId
        serviceProviderCredentials.credentialsMap = jacksonObjectMapper().writeValueAsString(emailSMTPConfig)
        serviceProviderCredentials.status= Status.ACTIVE
        emailSMTPConfig.serviceProviderCredentialsId = saveServiceProviderCredentials(serviceProviderCredentials)
        return emailSMTPConfig
    }

    fun saveSESEmailProviderCredentials(clientId: Long, appUserId: Long, emailSESConfig: EmailSESConfig): EmailSESConfig {
        var serviceProviderCredentials: ServiceProviderCredentials = ServiceProviderCredentials()
        serviceProviderCredentials.serviceProvider= ServiceProvider.AWS_SES
        serviceProviderCredentials.serviceProviderType= ServiceProviderType.EMAIL_SERVICE_PROVIDER
        serviceProviderCredentials.clientID=clientId
        serviceProviderCredentials.appuserID=appUserId
        serviceProviderCredentials.credentialsMap = jacksonObjectMapper().writeValueAsString(emailSESConfig)
        serviceProviderCredentials.status= Status.ACTIVE
        emailSESConfig.serviceProviderCredentialsId = saveServiceProviderCredentials(serviceProviderCredentials)
        return emailSESConfig
    }

    fun saveSnsSmsProviderCredentials(clientId: Long, appUserId: Long, emailSESConfig: EmailSESConfig): EmailSESConfig {
        var serviceProviderCredentials: ServiceProviderCredentials = ServiceProviderCredentials()
        serviceProviderCredentials.serviceProvider= ServiceProvider.AWS_SNS
        serviceProviderCredentials.serviceProviderType= ServiceProviderType.SMS_SERVICE_PROVIDER
        serviceProviderCredentials.clientID=clientId
        serviceProviderCredentials.appuserID=appUserId
        serviceProviderCredentials.credentialsMap = jacksonObjectMapper().writeValueAsString(emailSESConfig)
        serviceProviderCredentials.status= Status.ACTIVE
        emailSESConfig.serviceProviderCredentialsId = saveServiceProviderCredentials(serviceProviderCredentials)
        return emailSESConfig
    }*/

    /*private fun saveServiceProviderCredentials(serviceProviderCredentials: ServiceProviderCredentials): Long {
        val saved = serviceProviderCredentialsRepository.save(serviceProviderCredentials)
        return saved.id!!
    }*/

    fun buildServiceProviderCredentials(webServiceProviderCredentials: WebServiceProviderCredentials): ServiceProviderCredentials {
        val spCreds = ServiceProviderCredentials()
        with(spCreds) {
            spCreds.appuserID = webServiceProviderCredentials.appuserID
            spCreds.clientID = webServiceProviderCredentials.clientID
            spCreds.dateCreated = webServiceProviderCredentials.dateCreated
            spCreds.dateModified = webServiceProviderCredentials.dateModified
            spCreds.id = webServiceProviderCredentials.id
            spCreds.serviceProvider = webServiceProviderCredentials.serviceProvider
            spCreds.serviceProviderType = webServiceProviderCredentials.serviceProviderType
            spCreds.status = webServiceProviderCredentials.status
            spCreds.credentialsMap = GsonBuilder().create().toJson(webServiceProviderCredentials.credentialsMap)
        }
        return spCreds
    }

    fun buildWebServiceProviderCredentials(serviceProviderCredentials: ServiceProviderCredentials): WebServiceProviderCredentials {
        val wspCreds = WebServiceProviderCredentials()
        with(wspCreds) {
            wspCreds.appuserID = serviceProviderCredentials.appuserID
            wspCreds.clientID = serviceProviderCredentials.clientID
            wspCreds.dateCreated = serviceProviderCredentials.dateCreated
            wspCreds.dateModified = serviceProviderCredentials.dateModified
            wspCreds.id = serviceProviderCredentials.id
            wspCreds.serviceProvider = serviceProviderCredentials.serviceProvider
            wspCreds.serviceProviderType = serviceProviderCredentials.serviceProviderType
            wspCreds.status = serviceProviderCredentials.status
            wspCreds.credentialsMap = com.google.gson.Gson().fromJson<HashMap<String, String>>(serviceProviderCredentials.credentialsMap, HashMap<String, String>().javaClass)
        }
        return wspCreds
    }
}