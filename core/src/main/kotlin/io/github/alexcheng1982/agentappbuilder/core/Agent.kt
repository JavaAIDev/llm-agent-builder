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
) : Plannable

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


interface Agent<in REQUEST : AgentRequest, out RESPONSE> {
    fun name(): String
    fun description(): String
    fun call(request: REQUEST): RESPONSE
}