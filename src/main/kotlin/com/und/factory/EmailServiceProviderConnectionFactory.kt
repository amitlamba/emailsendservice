package com.und.factory

import com.und.model.EmailSMTPConfig
import com.und.model.ServiceProviderType
import com.und.model.Status
import com.und.repository.ServiceProviderCredentialsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.mail.Session
import javax.mail.Transport

@Component
class EmailServiceProviderConnectionFactory {

    @Autowired
    lateinit private var serviceProviderCredentialsRepository: ServiceProviderCredentialsRepository

    var emailSMPTConfigs: MutableMap<Long, EmailSMTPConfig> = mutableMapOf()
    var emailSMTPSessions: MutableMap<Long, Session> = mutableMapOf()
    var emailSMTPTransportConnections: MutableMap<Long, Transport> = mutableMapOf()

    fun getEmailServiceProvider(clientID: Long): EmailSMTPConfig {
        synchronized(clientID) {
            if (!emailSMPTConfigs.containsKey(clientID)) {
                val serviceProviderCreds = serviceProviderCredentialsRepository.findByClientIDAndServiceProviderTypeAndStatus(clientID, ServiceProviderType.EMAIL_SERVICE_PROVIDER, Status.ACTIVE).first()
                var emailSMTPConfig = EmailSMTPConfig(serviceProviderCreds.id, clientID, serviceProviderCreds.url, serviceProviderCreds.port!!,
                        serviceProviderCreds.username, serviceProviderCreds.password)
                emailSMPTConfigs.put(clientID, emailSMTPConfig)
            }
            return emailSMPTConfigs.get(clientID)!!
        }
    }

    fun getSMTPSession(clientID: Long, emailSMTPConfig: EmailSMTPConfig? = null): Session {
        synchronized(clientID) {
            if (!emailSMTPSessions.containsKey(clientID)) {
                var emailSMTPConfigVar: EmailSMTPConfig? = emailSMTPConfig
                if(emailSMTPConfig == null) {
                    emailSMTPConfigVar = getEmailServiceProvider(clientID)
                }
                val session = createSMTPSession(emailSMTPConfigVar!!)
                emailSMTPSessions.put(clientID, session)
            }
            return emailSMTPSessions.get(clientID)!!
        }
    }

    fun getSMTPTransportConnection(clientID: Long): Transport {
        synchronized(clientID) {
            if (!emailSMTPTransportConnections.containsKey(clientID)) {
                val transport = getSMTPSession(clientID).getTransport()
                val esp = getEmailServiceProvider(clientID)
                transport.connect(esp.HOST, esp.SMTP_USERNAME, esp.SMTP_PASSWORD)
                emailSMTPTransportConnections.put(clientID, transport)
            }
            return emailSMTPTransportConnections.get(clientID)!!
        }
    }

    fun closeSMTPTransportConnection(clientID: Long) {
        synchronized(clientID) {
            if (emailSMTPTransportConnections.containsKey(clientID)) {
                try {
                    emailSMTPTransportConnections.get(clientID)!!.close()
                } finally {
                    emailSMTPTransportConnections.remove(clientID)
                }
            }
        }
    }

    private fun createSMTPSession(emailSMTPConfig: EmailSMTPConfig): Session {
        // Create a Properties object to contain connection configuration information.
        val props = System.getProperties()
        props.put("mail.transport.protocol", "smtp")
        props.put("mail.smtp.port", emailSMTPConfig.PORT)
        props.put("mail.smtp.starttls.enable", "true")
        props.put("mail.smtp.auth", "true")

        // Create a Session object to represent a mail session with the specified properties.
        val session = Session.getDefaultInstance(props)
        return session
    }
}