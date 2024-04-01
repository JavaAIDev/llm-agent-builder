package cc.vividcode.ai.agentappbuilder.core.planner

import cc.vividcode.ai.agentappbuilder.core.AgentAction
import cc.vividcode.ai.agentappbuilder.core.AgentFinish

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

class OutputParserException(text: String) : Exception(text)