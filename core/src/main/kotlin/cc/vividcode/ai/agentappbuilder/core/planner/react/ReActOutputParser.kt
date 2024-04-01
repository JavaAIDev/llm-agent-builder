package cc.vividcode.ai.agentappbuilder.core.planner.react

import cc.vividcode.ai.agentappbuilder.core.AgentAction
import cc.vividcode.ai.agentappbuilder.core.AgentFinish
import cc.vividcode.ai.agentappbuilder.core.planner.OutputParser
import cc.vividcode.ai.agentappbuilder.core.planner.OutputParserException
import cc.vividcode.ai.agentappbuilder.core.planner.ParseResult
import java.util.regex.Pattern

class ReActOutputParser : OutputParser {
    private val finalAnswerAction = "Final Answer:"
    private val pattern =
        Pattern.compile(
            ".*Action\\s*\\d*\\s*:\\s*(.*?)\\s*Action\\s*\\d*\\s*Input\\s*\\d*\\s*:\\s*(.*)",
            Pattern.DOTALL or Pattern.MULTILINE or Pattern.CASE_INSENSITIVE
        )

    override fun parse(text: String): ParseResult {
        val actionMatch = pattern.matcher(text)
        if (actionMatch.matches()) {
            val action = actionMatch.group(1).trim()
            val actionInput = actionMatch.group(2)
            val toolInput = actionInput.trim(' ').trim('"')
            return ParseResult.action(
                AgentAction(
                    action,
                    toolInput,
                    text
                )
            )
        } else if (text.contains(finalAnswerAction)) {
            return ParseResult.finish(
                AgentFinish(
                    mapOf(
                        "output" to text.split(finalAnswerAction).last()
                            .trim()
                    ),
                    text
                )
            )
        }

        throw OutputParserException("Could not parse LLM output: $text")
    }
}