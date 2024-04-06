package io.github.alexcheng1982.agentappbuilder.core.planner.structuredchat

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.alexcheng1982.agentappbuilder.core.AgentAction
import io.github.alexcheng1982.agentappbuilder.core.AgentFinish
import io.github.alexcheng1982.agentappbuilder.core.planner.OutputParser
import io.github.alexcheng1982.agentappbuilder.core.planner.OutputParserException
import io.github.alexcheng1982.agentappbuilder.core.planner.ParseResult
import java.util.regex.Pattern

class StructuredChatOutputParser : OutputParser {
    private val pattern =
        Pattern.compile(
            ".*```(?:json\\s+)?(\\W.*?)```",
            Pattern.DOTALL or Pattern.MULTILINE or Pattern.CASE_INSENSITIVE
        )

    private val objectMapper = ObjectMapper()

    override fun parse(text: String): ParseResult {
        val actionMatch = pattern.matcher(text)
        if (actionMatch.matches()) {
            val json = actionMatch.group(1).trim()
            val response =
                objectMapper.readValue(json, ActionResponse::class.java)
            return if (response.action == "Final Answer") {
                ParseResult.finish(
                    AgentFinish(
                        mapOf(
                            "output" to (response.actionInput ?: "")
                        ), text
                    )
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
        }
        throw OutputParserException("Could not parse LLM output: $text")
    }

    companion object {
        val INSTANCE = StructuredChatOutputParser()
    }
}