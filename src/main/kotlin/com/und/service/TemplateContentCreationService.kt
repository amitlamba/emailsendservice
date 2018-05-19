package com.und.service

import com.und.utils.loggerFor
import freemarker.template.Configuration
import freemarker.template.Template
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils
import java.net.URLEncoder
import java.util.regex.Pattern

@Service
class TemplateContentCreationService {

    companion object {
        val logger = loggerFor(TemplateContentCreationService::class.java)
    }

    @Autowired
    private lateinit var fmConfiguration: Configuration
    val urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)"
    val trackingURL = "https://userndot.com/event/track"
    val excludeTrackingURLs = arrayOf(
            "^(https?|ftp)://userndot.com.*\$"
    )

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

    fun trackAllURLs(content: String, clientId: Long, mongoEmailId: String): String {
        val containedUrls = ArrayList<String>()
        val pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE)
        val urlMatcher = pattern.matcher(content)

        while (urlMatcher.find()) {
            containedUrls.add(content.substring(urlMatcher.start(0),
                    urlMatcher.end(0)))
        }

        var replacedContent = content
        for(c in containedUrls) {
            var skip = false
            for(exclude in excludeTrackingURLs) {
                if (c.matches(exclude.toRegex())) {
                    skip = true
                    break
                }
            }
            if( skip )
                continue
            replacedContent = replacedContent.replace(c, "$trackingURL?c=$clientId&e=$mongoEmailId&u="+ URLEncoder.encode(c,"UTF-8"))
        }
        return replacedContent
    }
}