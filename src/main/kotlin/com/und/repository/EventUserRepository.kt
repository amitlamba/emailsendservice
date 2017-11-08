package com.und.eventapi.repository

import com.und.eventapi.model.Event
import com.und.eventapi.model.EventUser
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface EventUserRepository : MongoRepository<EventUser, String> {


}