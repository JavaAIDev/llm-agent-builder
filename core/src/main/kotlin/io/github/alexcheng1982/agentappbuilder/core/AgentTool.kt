package io.github.alexcheng1982.agentappbuilder.core

import java.util.function.Function

interface AgentTool<REQUEST, RESPONSE> : Function<REQUEST, RESPONSE> {
    fun name(): String
    fun description(): String
}


class ExceptionTool : AgentTool<String, String> {
    override fun name(): String {
        return "_Exception"
    }

    override fun description(): String {
        return "Exception tool"
    }

    override fun apply(t: String): String {
        return t
    }

}