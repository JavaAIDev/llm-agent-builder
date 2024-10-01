package io.github.llmagentbuilder.planner.simple

import io.github.llmagentbuilder.core.AgentFinish
import io.github.llmagentbuilder.core.planner.OutputParser
import io.github.llmagentbuilder.core.planner.ParseResult

/**
 * Output from LLM is used as a return value directly, no further actions will be taken.
 *
 * @see AgentFinish
 */
class SimpleOutputParser : OutputParser {
    override fun parse(text: String): ParseResult {
        return ParseResult.finishWithText(text)
    }

    companion object {
        val INSTANCE = SimpleOutputParser()
    }
}