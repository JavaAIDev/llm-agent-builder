package io.github.llmagentbuilder.core.planner.planner

import io.github.llmagentbuilder.core.AgentAction
import io.github.llmagentbuilder.core.AgentFinish
import java.util.function.Function

data class ParseResult(
    val action: AgentAction? = null,
    val finish: AgentFinish? = null,
) {
    companion object {
        fun action(action: AgentAction) = ParseResult(action = action)
        fun finish(finish: AgentFinish) = ParseResult(finish = finish)
        fun finishWithText(text: String) =
            ParseResult(finish = AgentFinish.fromOutput(text, text))

        fun finishWithOutputAndLog(output: String, log: String) =
            ParseResult(finish = AgentFinish.fromOutput(output, log))
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
    private val sendToLlm: Boolean = false,
    override val cause: Throwable? = null,
) :
    Exception(message, cause) {
    fun llmOutput() = (if (sendToLlm) llmOutput else message) ?: ""
    fun observation() =
        if (sendToLlm) observation ?: "" else "Invalid or incomplete response"
}

interface OutputParserExceptionHandler : Function<OutputParserException, String>
