package com.javaaidev.llmagentbuilder.planner.simple

import com.javaaidev.llmagentbuilder.core.AgentFinish
import com.javaaidev.llmagentbuilder.core.planner.OutputParser
import com.javaaidev.llmagentbuilder.core.planner.ParseResult

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