package com.und.repository

import com.und.model.jpa.Segment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface SegmentRepository: JpaRepository<Segment, Long> {

    fun getSegmentByIdAndClientID(segmentId: Long, clientID: Long): Segment
}