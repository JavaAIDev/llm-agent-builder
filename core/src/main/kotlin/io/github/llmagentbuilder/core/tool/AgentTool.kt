package io.github.llmagentbuilder.core.tool

import java.util.function.Function

/**
 * Agent tool
 */
interface AgentTool<REQUEST, RESPONSE> : Function<REQUEST, RESPONSE> {
    /**
     * Name of the agent tool, will be passed to LLM
     */
    fun name(): String

    /**
     * Description of the agent tool, will be passed to LLM
     */
    fun description(): String
}

interface ConfigurableAgentTool<REQUEST, RESPONSE, CONFIG> :
    AgentTool<REQUEST, RESPONSE> {

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