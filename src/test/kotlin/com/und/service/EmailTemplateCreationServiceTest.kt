package com.und.service

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class EmailTemplateCreationServiceTest {

    @Autowired
    private lateinit var emailTemplateCreationService: EmailTemplateCreationService

    @Test
    fun testEmailContentCreation() {
        val content = "Hello, \${name}"
        println("content: ${content}")
        val name = "Sample Template"
        val converted = emailTemplateCreationService.getContentFromTemplate(name, content, mapOf(Pair("name", "Amit")))
        println("converted: ${converted}")
    }
}