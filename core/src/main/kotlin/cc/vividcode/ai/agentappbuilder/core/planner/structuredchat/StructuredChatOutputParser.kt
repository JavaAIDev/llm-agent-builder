package cc.vividcode.ai.agentappbuilder.core.planner.structuredchat

import cc.vividcode.ai.agentappbuilder.core.*
import com.fasterxml.jackson.databind.ObjectMapper
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
            val response = objectMapper.readValue(json, ActionResponse::class.java)
            return if (response.action == "Final Answer") {
                ParseResult.finish(
                    AgentFinish(
                        mapOf(
                            "output" to (response.actionInput ?: "")
                        ), text
                    )
                )
            } else {
                ParseResult.action(AgentAction(response.action, response.actionInput, text))
            }
        }
        throw OutputParserException("Could not parse LLM output: $text")
    }
}