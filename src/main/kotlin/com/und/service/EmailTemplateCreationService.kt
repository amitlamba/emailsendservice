package com.und.service

import com.und.utils.loggerFor
import freemarker.template.Configuration
import freemarker.template.Template
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils

@Service
class EmailTemplateCreationService {

    companion object {
        val logger = loggerFor(EmailTemplateCreationService::class.java)
    }

    @Autowired
    private lateinit var fmConfiguration: Configuration

    fun getContentFromTemplate(name: String, templateContent: String, model: Map<String, Any>): String {
        val content = StringBuffer()

        try {
            var template: Template = Template(name, templateContent, fmConfiguration)
            content.append(FreeMarkerTemplateUtils.processTemplateIntoString(template, model))
        } catch (e: Exception) {
            logger.error(e.message)
        }

        return content.toString()
    }
}