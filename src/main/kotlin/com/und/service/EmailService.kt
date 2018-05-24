package com.und.service

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.Body
import com.amazonaws.services.simpleemail.model.Content
import com.amazonaws.services.simpleemail.model.Destination
import com.amazonaws.services.simpleemail.model.SendEmailRequest
import com.und.factory.EmailServiceProviderConnectionFactory
import com.und.model.mongo.EmailStatus.NOT_SENT
import com.und.model.mongo.EmailStatus.SENT
import com.und.model.utils.Email
import com.und.model.utils.EmailSESConfig
import com.und.model.utils.EmailSMTPConfig
import com.und.model.utils.ServiceProviderCredentials
import com.und.utils.loggerFor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import com.amazonaws.services.simpleemail.model.Message as SESMessage


@Service
class EmailService {
    companion object {
        protected val logger = loggerFor(EmailService::class.java)
    }


    @Autowired
    private lateinit var serviceProviderCredentialsService: ServiceProviderCredentialsService


    @Autowired
    private lateinit var emailHelperService: EmailHelperService

    @Autowired
    private lateinit var emailSendService: EmailSendService

    private var wspCredsMap: MutableMap<Long, ServiceProviderCredentials> = mutableMapOf()


    fun sendEmailBySMTP(emailSMTPConfig: EmailSMTPConfig, email: Email){
        emailSendService.sendEmailBySMTP(emailSMTPConfig,email)
    }
    fun sendEmailByAWSSDK(emailSESConfig: EmailSESConfig, email: Email) {
        emailSendService.sendEmailByAWSSDK(emailSESConfig, email)
    }

    fun sendEmail(email: Email) {
        val emailToSend = emailHelperService.updateSubjectAndBody(email)
        val serviceProviderCredential = serviceProviderCredentials(email = emailToSend)
        sendEmail(serviceProviderCredential, email)
        emailHelperService.saveMailInMongo(email, SENT)
    }

    private fun sendEmail(serviceProviderCredential: ServiceProviderCredentials, email: Email) {
        when (serviceProviderCredential.serviceProvider) {
            ServiceProviderCredentialsService.ServiceProvider.SMTP.desc,
            ServiceProviderCredentialsService.ServiceProvider.AWS_SES_SMTP.desc -> {
                val emailSMTPConfig = EmailSMTPConfig.build(serviceProviderCredential)
                sendEmailBySMTP(emailSMTPConfig, email)
            }
            ServiceProviderCredentialsService.ServiceProvider.AWS_SES_API.desc -> {
                val emailSESConfig = EmailSESConfig.build(serviceProviderCredential)
                sendEmailByAWSSDK(emailSESConfig, email)
            }

        }
    }

    private fun serviceProviderCredentials(email: Email): ServiceProviderCredentials {
        synchronized(email.clientID) {
            //TODO: This code can be cached in Redis
            if (!wspCredsMap.containsKey(email.clientID)) {
                val webServiceProviderCred =serviceProviderCredentialsService.getServiceProviderCredentials(email)
                wspCredsMap[email.clientID] = webServiceProviderCred
            }
        }
       return  wspCredsMap[email.clientID]!!
    }




}
