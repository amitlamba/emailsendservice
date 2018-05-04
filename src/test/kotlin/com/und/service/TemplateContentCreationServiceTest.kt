package com.und.service

import com.und.model.mongo.EventUser
import com.und.model.mongo.Identity
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class TemplateContentCreationServiceTest {

    @Autowired
    private lateinit var templateContentCreationService: TemplateContentCreationService

    @Test
    fun testEmailContentCreation() {
        val content = "Hello, \${user.firstname} \${user.lastname}"
        println("content: ${content}")
        val name = "Sample Template"
        val converted = templateContentCreationService.getContentFromTemplate(name, content, getModelMap())
        println("converted: ${converted}")
        assert(converted.contentEquals("Hello, Amit Lamba"))
    }

    fun getModelMap(): Map<String, Any> {
        var map: MutableMap<String, Any> = mutableMapOf()
        map.put("user",EventUser())
        println("map: ${map}")
        return map
    }
}