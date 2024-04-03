package io.github.alexcheng1982.agentappbuilder.core.planner.zeroshot

import io.github.alexcheng1982.agentappbuilder.core.AgentFinish
import io.github.alexcheng1982.agentappbuilder.core.planner.OutputParser
import io.github.alexcheng1982.agentappbuilder.core.planner.ParseResult

class ZeroShotOutputParser : OutputParser {
    override fun parse(text: String): ParseResult {
        return ParseResult.finish(
            AgentFinish(
                mapOf("output" to text),
                text,
            )
        )
    }
}