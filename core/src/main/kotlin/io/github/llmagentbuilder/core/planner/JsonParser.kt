package io.github.llmagentbuilder.core.planner

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.github.llmagentbuilder.core.utils.JsonEscapeResult
import io.github.llmagentbuilder.core.utils.JsonUtils
import org.slf4j.LoggerFactory
import java.util.regex.Pattern

object JsonParser {
    private val pattern =
        Pattern.compile(
            "^.*?`{3}(?:json)?\\n?(.*?)`{3}.*?\$",
            Pattern.DOTALL or Pattern.MULTILINE
        )
    private val actionInputPattern =
        Pattern.compile(
            "(\"action_input\":\\s*\")(.*?)(\")",
            Pattern.DOTALL or Pattern.MULTILINE
        )

    private val logger = LoggerFactory.getLogger(JsonParser::class.java)

    private val objectMapper =
        ObjectMapper().registerModule(KotlinModule.Builder().build())

    fun parse(json: String): Map<String, Any>? {
        try {
            return parseJson(parseJsonMarkdown(json))
        } catch (e: Exception) {
            logger.warn("Failed to parse json: {}", json, e)
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
        jsonString = cleanJson(jsonString)
        val escapeResult = cleanTextBlock(jsonString)
        jsonString = if (escapeResult.escaped) {
            escapeResult.result
        } else {
            cleanQuotes(jsonString).result
        }
        return jsonString
    }

    private fun cleanJson(json: String): String {
        val matcher = actionInputPattern.matcher(json)
        if (matcher.matches()) {
            val value = JsonUtils.collapseLines(matcher.group(2))
                .replace("(?<!\\\\)\"", "\\\"")
            return matcher.group(1) + value + matcher.group(3)
        }
        return json
    }

    private fun cleanTextBlock(json: String): JsonEscapeResult {
        val block = "\"\"\""
        return JsonUtils.escapeJsonBetween(json, block, block, "\"", "\"", true)
    }

    private fun cleanQuotes(input: String): JsonEscapeResult {
        val unescaped = input.replace("\\\"", "'")
        return JsonUtils.escapeJsonBetween(unescaped, "\"", "\"", "\"", "\"")
    }

}