package com.und.service

import com.google.gson.Gson
import com.und.model.jpa.Segment
import com.und.model.mongo.EventUser
import com.und.model.mongo.Identity
import com.und.repository.SegmentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import com.und.model.utils.Segment as WebSegment

@Service
class SegmentService {

    @Autowired
    private lateinit var segmentRepository: SegmentRepository

    fun getSegment(segmentId: Long, clientId: Long): Segment {
        val segment = segmentRepository.getSegmentByIdAndClientID(segmentId, clientId)
        return segment
    }

    private fun buildWebSegment(segment: Segment): WebSegment {
        val websegment = Gson().fromJson(segment.data, WebSegment::class.java)
        with(websegment) {
            id = segment.id
            name = segment.name
            type = segment.type
        }
        return websegment
    }

    fun getWebSegment(segmentId: Long, clientId: Long): WebSegment {
        val segment = getSegment(segmentId, clientId)
        val webSegment = buildWebSegment(segment)
        return webSegment
    }

    fun getUserData(webSegment: WebSegment): List<EventUser> {
        //TODO: Write the definition to get data from Mongo here
        return listOf(createEventUser())
    }

    fun createEventUser(): EventUser {

        val eventUserDb = EventUser()
        with(eventUserDb.standardInfo) {

            firstname = "Amit"
            lastname = "Lamba"
            country = "India"
        }
        with(eventUserDb.identity) {
            clientUserId = "100"
            email = "amit@userndot.com"
        }

        return eventUserDb
    }
}