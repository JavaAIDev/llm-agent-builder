package cc.vividcode.ai.agentappbuilder.core.planner

import cc.vividcode.ai.agentappbuilder.core.AgentAction
import cc.vividcode.ai.agentappbuilder.core.AgentFinish
import java.util.function.Function

data class ParseResult(
    val action: AgentAction? = null,
    val finish: AgentFinish? = null,
) {
    companion object {
        fun action(action: AgentAction) = ParseResult(action = action)
        fun finish(finish: AgentFinish) = ParseResult(finish = finish)
    }
}

interface OutputParser {
    @Throws(OutputParserException::class)
    fun parse(text: String): ParseResult
}

class OutputParserException(
    message: String,
    private val observation: String? = null,
    private val llmOutput: String? = null,
    private val sendToLlm: Boolean = false
) :
    Exception(message) {
    fun llmOutput() = (if (sendToLlm) llmOutput else message) ?: ""
    fun observation() =
        if (sendToLlm) observation ?: "" else "Invalid or incomplete response"
}

interface OutputParserExceptionHandler : Function<OutputParserException, String>
