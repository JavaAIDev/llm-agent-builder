package io.github.alexcheng1982.llmagentbuilder.core.planner

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.slf4j.LoggerFactory
import java.util.regex.Pattern

object JsonParser {
    private val pattern =
        Pattern.compile("^.*?`{3}(?:json)?\\n?(.*?)`{3}.*?\$", Pattern.DOTALL)
    private val actionInputPattern =
        Pattern.compile("(\"action_input\":\\s*\")(.*?)(\")", Pattern.DOTALL)

    private val logger = LoggerFactory.getLogger(JsonParser::class.java)

    private val objectMapper =
        ObjectMapper().registerModule(KotlinModule.Builder().build())

    fun parse(json: String): Map<String, Any>? {
        try {
            return parseJson(parseJsonMarkdown(json))
        } catch (e: Exception) {
            logger.warn("Failed to parse json {}", json)
            return null
        }
    }

    private fun parseJson(json: String): Map<String, Any> {
        return objectMapper.readValue(
            json,
            object : TypeReference<Map<String, Any>>() {})
    }

    private fun parseJsonMarkdown(json: String): String {
        val matcher = pattern.matcher(json)
        var jsonString = if (matcher.matches()) {
            matcher.group(1)
        } else json
        jsonString = jsonString.trim().trim('`')
        return cleanJson(jsonString)
    }

    private fun cleanJson(json: String): String {
        val matcher = actionInputPattern.matcher(json)
        if (matcher.matches()) {
            val value = matcher.group(2)
                .replace("\\n", "\\\\n")
                .replace("\\r", "\\\\r")
                .replace("\\t", "\\\\t")
                .replace("(?<!\\\\)\"", "\\\"")
            return matcher.group(1) + value + matcher.group(3)
        }
        return json
    }
}