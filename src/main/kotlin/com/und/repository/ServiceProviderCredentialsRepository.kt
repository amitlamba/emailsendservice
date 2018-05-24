package com.und.repository
import com.und.model.jpa.ServiceProviderCredentials
import com.und.model.jpa.Status
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface ServiceProviderCredentialsRepository : JpaRepository<ServiceProviderCredentials, Long> {
    fun findByClientIDAndServiceProviderTypeAndStatus(clientID: Long, serviceProviderType: String, status: Status)
            : List<ServiceProviderCredentials>

    fun findTop1ByClientIDAndServiceProviderTypeAndStatus(clientID: Long, serviceProviderType: String, status: Status)
            : Optional<ServiceProviderCredentials>




}