package cc.vividcode.ai.agentappbuilder.core.planner.react

import cc.vividcode.ai.agentappbuilder.core.*
import java.util.regex.Pattern

class ReActOutputParser : OutputParser {
    private val finalAnswerSection = "Final Answer:"
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
        } else if (text.contains(finalAnswerSection)) {
            return ParseResult.finish(
                AgentFinish(
                    mutableMapOf(
                        "output" to text.split(finalAnswerSection).last()
                            .trim()
                    ),
                    text
                )
            )
        }

        throw OutputParserException("Could not parse LLM output: $text")
    }
}