package io.github.alexcheng1982.agentappbuilder.core

import io.github.alexcheng1982.agentappbuilder.core.executor.ActionPlanningResult

sealed interface Plannable

data class AgentAction(
    val tool: String,
    val toolInput: String,
    val log: String,
) : Plannable

data class AgentStep(
    val action: AgentAction,
    val observation: Any,
) : Plannable

data class AgentFinish(
    val returnValues: Map<String, Any>,
    val log: String,
) : Plannable {
    companion object {
        fun fromOutput(output: String, log: String) = AgentFinish(
            mapOf("output" to output),
            log
        )
    }
}

data class IntermediateAgentStep(
    val action: AgentAction,
    val observation: String,
)

interface Planner {
    fun plan(
        inputs: Map<String, Any>,
        intermediateSteps: List<IntermediateAgentStep>
    ): ActionPlanningResult

    fun returnStoppedResponse(
        earlyStoppingMethod: String?,
        intermediateSteps: List<IntermediateAgentStep>
    ): AgentFinish {
        if ("force" == earlyStoppingMethod) {
            return AgentFinish(
                mapOf("output" to ""),
                ""
            )
        } else {
            throw IllegalArgumentException("Unsupported early stopping method $earlyStoppingMethod")
        }
    }
}

interface AgentRequest {
    fun toMap(): Map<String, Any>
}

/**
 * LLM based agent
 */
interface Agent<in REQUEST : AgentRequest, out RESPONSE> {
    /**
     * Name of the agent
     */
    fun name(): String

    /**
     * Description of the agent
     */
    fun description(): String

    /**
     * Usage instruction for user to interact with the agent
     */
    fun usageInstruction() = "Ask me anything"

    /**
     * Call the agent and get the response
     */
    fun call(request: REQUEST): RESPONSE
}

data class ChatAgentRequest(val input: String, val memoryId: String? = null) :
    AgentRequest {
    override fun toMap(): Map<String, Any> {
        return mapOf("input" to input) + (memoryId?.let { mapOf("memory_id" to it) }
            ?: mapOf())
    }
}

data class ChatAgentResponse(val output: String) {
    companion object {
        fun fromMap(map: Map<String, Any>): ChatAgentResponse {
            return ChatAgentResponse(map["output"]?.toString() ?: "")
        }
    }
}

interface ChatAgent : Agent<ChatAgentRequest, ChatAgentResponse>