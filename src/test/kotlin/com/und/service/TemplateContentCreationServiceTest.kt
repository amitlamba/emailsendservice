package com.und.service

import com.und.model.mongo.EventUser
import freemarker.template.Configuration
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.MockitoAnnotations
import org.springframework.test.util.ReflectionTestUtils
import org.hamcrest.CoreMatchers.`is` as Is

class TemplateContentCreationServiceTest {

    @InjectMocks
    private lateinit var templateContentCreationService: TemplateContentCreationService

    private  var fmConfiguration: Configuration = Configuration()


    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        ReflectionTestUtils.setField(templateContentCreationService, "fmConfiguration", fmConfiguration)

    }

    @Test
    fun testEmailContentCreation() {
        val content = "Hello, \${ user.standardInfo.firstname} \${ user.standardInfo.lastname}"
        val name = "Sample Template"
        val converted = templateContentCreationService.getContentFromTemplate(name, content, getModelMap())
        Assert.assertThat(converted, Is("Hello, Amit Lamba"))
    }

    fun getModelMap(): Map<String, Any> {
        val user = EventUser()
        user.standardInfo.firstname = "Amit"
        user.standardInfo.lastname = "Lamba"
       // return  mutableMapOf("firstname" to "Amit", "lastname" to "Lamba")
        return  mutableMapOf("user" to user)
    }
}