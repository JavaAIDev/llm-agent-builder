package io.github.alexcheng1982.agentappbuilder.core.planner.nofeedback

import io.github.alexcheng1982.agentappbuilder.core.AgentFinish
import io.github.alexcheng1982.agentappbuilder.core.planner.OutputParser
import io.github.alexcheng1982.agentappbuilder.core.planner.ParseResult

/**
 * Output from LLM is used as a return value directly, no further actions will be taken.
 *
 * @see AgentFinish
 */
class NoFeedbackOutputParser : OutputParser {
    override fun parse(text: String): ParseResult {
        return ParseResult.finishWithText(text)
    }

    companion object {
        val INSTANCE = NoFeedbackOutputParser()
    }
}