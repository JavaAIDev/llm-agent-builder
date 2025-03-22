package com.javaaidev.llmagentbuilder.core.utils

import org.apache.commons.text.StringEscapeUtils

object JsonUtils {
    fun escapeStringContent(input: String): String {
        return escapeJsonBetween(
            input,
            "\"",
            "\"",
            "\"",
            "\""
        ).result
    }

    fun escapeJsonBetween(
        json: String,
        startText: String,
        endText: String,
        startReplace: String,
        endReplace: String,
        replaceOnce: Boolean = false,
    ): JsonEscapeResult {
        var escaped = false
        val builder = StringBuilder()
        var start = -1
        var lastStart = 0
        while (true) {
            start = json.indexOf(startText, lastStart)
            if (start == -1) {
                builder.append(json.substring(lastStart))
                break
            }
            val end =
                if (replaceOnce) json.lastIndexOf(endText) else json.indexOf(
                    endText,
                    start + startText.length
                )
            if (end != -1 && start != end) {
                val value =
                    escapeJson(
                        collapseLines(
                            json.substring(
                                start + startText.length,
                                end
                            )
                        )
                    )
                escaped = true
                builder.append(
                    json.substring(
                        lastStart,
                        start
                    )
                ).append(startReplace)
                    .append(value)
                    .append(endReplace)
                lastStart = end + endText.length
            }
        }
        return JsonEscapeResult(builder.toString(), escaped)
    }

    private fun escapeJson(input: String): String {
        return StringEscapeUtils.escapeJson(input)
    }

    fun collapseLines(input: String): String {
        return input.replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }
}