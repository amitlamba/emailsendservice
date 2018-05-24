package com.und.repository

import com.und.model.jpa.EmailTemplate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailTemplateRepository : JpaRepository<EmailTemplate, Long> {
}