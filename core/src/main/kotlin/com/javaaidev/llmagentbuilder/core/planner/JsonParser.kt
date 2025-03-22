package com.javaaidev.llmagentbuilder.core.planner

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.javaaidev.llmagentbuilder.core.utils.JsonEscapeResult
import com.javaaidev.llmagentbuilder.core.utils.JsonUtils
import org.slf4j.LoggerFactory
import java.util.regex.Pattern

interface JsonNormalizer {
    fun normalize(json: String): String
}

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

    private val jsonNormalizers = listOf(
        MarkdownJsonNormalizer(),
        BasicCleanJsonNormalizer(),
        EscapeJsonNormalizer()
    )

    fun parse(json: String): Map<String, Any>? {
        return normalizeAndParse(json).also {
            if (it == null) {
                logger.warn("Failed to parse json: {}", json)
            }
        }
    }

    private fun parseJson(json: String): Map<String, Any> {
        return objectMapper.readValue(
            json,
            object : TypeReference<Map<String, Any>>() {})
    }

    private fun normalizeAndParse(json: String): Map<String, Any>? {
        var jsonString = json
        for (jsonNormalizer in jsonNormalizers) {
            jsonString = jsonNormalizer.normalize(jsonString)
            try {
                return parseJson(jsonString)
            } catch (e: Exception) {
                continue
            }
        }
        return null
    }

    private class MarkdownJsonNormalizer : JsonNormalizer {
        override fun normalize(json: String): String {
            val matcher = pattern.matcher(json)
            return if (matcher.matches()) {
                matcher.group(1)
            } else json
        }
    }

    private class BasicCleanJsonNormalizer : JsonNormalizer {
        override fun normalize(json: String): String {
            var jsonString = json.trim().trim('`')
            jsonString = cleanJson(jsonString)
            return jsonString
        }

    }

    private class EscapeJsonNormalizer : JsonNormalizer {
        override fun normalize(json: String): String {
            val escapeResult = cleanTextBlock(json)
            val jsonString = if (escapeResult.escaped) {
                escapeResult.result
            } else {
                cleanQuotes(json).result
            }
            return jsonString
        }

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