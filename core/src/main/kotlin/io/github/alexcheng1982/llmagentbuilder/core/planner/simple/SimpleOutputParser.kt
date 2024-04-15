package io.github.alexcheng1982.llmagentbuilder.core.planner.simple

import io.github.alexcheng1982.llmagentbuilder.core.AgentFinish
import io.github.alexcheng1982.llmagentbuilder.core.planner.OutputParser
import io.github.alexcheng1982.llmagentbuilder.core.planner.ParseResult

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