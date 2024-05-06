package io.github.llmagentbuilder.core.planner.structuredchat

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.llmagentbuilder.core.AgentAction
import io.github.llmagentbuilder.core.planner.OutputParser
import io.github.llmagentbuilder.core.planner.OutputParserException
import io.github.llmagentbuilder.core.planner.ParseResult
import java.util.regex.Pattern

class StructuredChatOutputParser : OutputParser {
    private val pattern =
        Pattern.compile(
            ".*```(?:json\\s+)?(\\W.*?)```",
            Pattern.DOTALL or Pattern.MULTILINE or Pattern.CASE_INSENSITIVE
        )

    private val objectMapper = ObjectMapper()

    override fun parse(text: String): ParseResult {
        try {
            val actionMatch = pattern.matcher(text)
            if (actionMatch.matches()) {
                val json = actionMatch.group(1).trim()
                val response =
                    objectMapper.readValue(json, ActionResponse::class.java)
                return if (response.action == "Final Answer") {
                    ParseResult.finishWithOutputAndLog(
                        response.actionInput ?: "",
                        text
                    )
                } else {
                    ParseResult.action(
                        AgentAction(
                            response.action,
                            response.actionInput,
                            text
                        )
                    )
                }
            } else {
                return ParseResult.finishWithText(text)
            }
        } catch (e: Exception) {
            throw OutputParserException(
                "Could not parse LLM output: $text",
                cause = e
            )
        }
    }

    companion object {
        val INSTANCE = StructuredChatOutputParser()
    }
}