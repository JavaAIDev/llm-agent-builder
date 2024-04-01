package cc.vividcode.ai.agentappbuilder.core.planner.reactjson

import cc.vividcode.ai.agentappbuilder.core.AgentAction
import cc.vividcode.ai.agentappbuilder.core.AgentFinish
import cc.vividcode.ai.agentappbuilder.core.planner.OutputParser
import cc.vividcode.ai.agentappbuilder.core.planner.OutputParserException
import cc.vividcode.ai.agentappbuilder.core.planner.ParseResult
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.regex.Pattern

class ReactJsonOutputParser : OutputParser {
    private val pattern =
        Pattern.compile(
            ".*```(?:json\\s+)?(\\W.*?)```",
            Pattern.DOTALL or Pattern.MULTILINE or Pattern.CASE_INSENSITIVE
        )

    private val finalAnswerAction = "Final Answer:"

    private val objectMapper = ObjectMapper()

    override fun parse(text: String): ParseResult {
        val includeAnswer = text.contains(finalAnswerAction)
        val actionMatch = pattern.matcher(text)
        if (actionMatch.matches()) {
            val json = actionMatch.group(1).trim()
            val response =
                objectMapper.readValue(json, object : TypeReference<Map<String, Any>>() {})
            val actionInput = response["action_input"]?.let {
                objectMapper.writeValueAsString(it)
            } ?: "{}"
            return if (response["action"] == "Final Answer") {
                ParseResult.finish(
                    AgentFinish(
                        mapOf(
                            "output" to actionInput
                        ), text
                    )
                )
            } else {
                response["action"]?.let {
                    ParseResult.action(AgentAction(it.toString(), actionInput, text))
                } ?: throw OutputParserException("Invalid action")
            }
        } else {
            if (includeAnswer) {
                val output = text.split(finalAnswerAction).last().trim()
                return ParseResult.finish(AgentFinish(mapOf("output" to output), text))
            }
        }
        throw OutputParserException("Could not parse LLM output: $text")
    }
}