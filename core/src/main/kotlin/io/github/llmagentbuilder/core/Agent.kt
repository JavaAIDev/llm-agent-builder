package io.github.llmagentbuilder.core

import io.github.llmagentbuilder.core.executor.ActionPlanningResult
import java.util.*

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
        inputs: ChatAgentRequest,
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
interface Agent<in REQUEST, out RESPONSE> {
    /**
     * ID of the agent
     *
     * Default value is the agent name with a unique suffix.
     */
    fun id(): String =
        name() + "_" + UUID.randomUUID().toString().replace("-", "")

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

interface ChatAgent : Agent<ChatAgentRequest, ChatAgentResponse>