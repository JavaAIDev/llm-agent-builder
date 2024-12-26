package io.github.llmagentbuilder.planner.executor

import io.github.llmagentbuilder.core.AgentFinish
import io.github.llmagentbuilder.core.IntermediateAgentStep
import io.github.llmagentbuilder.core.Planner
import io.github.llmagentbuilder.core.executor.ActionPlanningResult
import io.github.llmagentbuilder.core.observation.AgentPlanningObservationContext
import io.github.llmagentbuilder.core.observation.AgentPlanningObservationDocumentation
import io.github.llmagentbuilder.core.observation.DefaultAgentPlanningObservationConvention
import io.github.llmagentbuilder.core.planner.OutputParser
import io.micrometer.observation.ObservationRegistry
import org.springframework.ai.chat.client.ChatClient

open class LLMPlanExecutor(
    private val chatClient: ChatClient,
    private val outputParser: OutputParser,
    private val observationRegistry: ObservationRegistry? = null,
) : Planner {
    override fun plan(
        inputs: Map<String, Any>,
        intermediateSteps: List<IntermediateAgentStep>
    ): ActionPlanningResult {
        val action = { internalPlan(inputs, intermediateSteps) }
        return observationRegistry?.let { registry ->
            instrumentedPlan(inputs, action, registry)
        } ?: action.invoke()
    }

    private fun internalPlan(
        inputs: Map<String, Any>,
        intermediateSteps: List<IntermediateAgentStep>
    ): ActionPlanningResult {
        val userInput =
            (inputs["input"] as? String)
                ?: throw RuntimeException("Invalid input")
        val thoughts = constructScratchpad(intermediateSteps)
        val response = chatClient.prompt()
            .user { spec ->
                spec.text(userInput).param("agent_scratchpad", thoughts)
            }
            .call().content()
        if (response?.isEmpty() != false) {
            return ActionPlanningResult.finish(
                AgentFinish.fromOutput(
                    "No response from LLM",
                    ""
                )
            )
        }
        val result = outputParser.parse(response)
        return ActionPlanningResult.fromParseResult(result)
    }

    private fun instrumentedPlan(
        input: Map<String, Any>,
        action: () -> ActionPlanningResult,
        registry: ObservationRegistry
    ): ActionPlanningResult {
        val observationContext =
            AgentPlanningObservationContext(input)
        val observation =
            AgentPlanningObservationDocumentation.AGENT_PLANNING.observation(
                null,
                DefaultAgentPlanningObservationConvention(),
                { observationContext },
                registry
            ).start()
        return try {
            observation.openScope().use {
                val response = action.invoke()
                observationContext.setResponse(response)
                response
            }
        } catch (e: Exception) {
            observation.error(e)
            throw e
        } finally {
            observation.stop()
        }
    }

    private fun constructScratchpad(intermediateSteps: List<IntermediateAgentStep>): String {
        return intermediateSteps.joinToString(" ") {
            val (action, observation) = it
            "${action.log} \nObservation: $observation\n"
        }
    }
}