package com.javaaidev.llmagentbuilder.planner.executor

import com.javaaidev.llmagentbuilder.core.*
import com.javaaidev.llmagentbuilder.core.executor.ActionPlanningResult
import com.javaaidev.llmagentbuilder.core.observation.AgentPlanningObservationContext
import com.javaaidev.llmagentbuilder.core.observation.AgentPlanningObservationDocumentation
import com.javaaidev.llmagentbuilder.core.observation.DefaultAgentPlanningObservationConvention
import com.javaaidev.llmagentbuilder.core.planner.OutputParser
import io.micrometer.observation.ObservationRegistry
import org.springframework.ai.chat.client.ChatClient

open class LLMPlanExecutor(
    private val chatClient: ChatClient,
    private val outputParser: OutputParser,
    private val observationRegistry: ObservationRegistry? = null,
) : Planner {
    override fun plan(
        inputs: ChatAgentRequest,
        intermediateSteps: List<IntermediateAgentStep>
    ): ActionPlanningResult {
        val action = { internalPlan(inputs, intermediateSteps) }
        return observationRegistry?.let { registry ->
            instrumentedPlan(inputs, action, registry)
        } ?: action.invoke()
    }

    private fun internalPlan(
        inputs: ChatAgentRequest,
        intermediateSteps: List<IntermediateAgentStep>
    ): ActionPlanningResult {
        val userInput =
            inputs.messages.filterIsInstance<ThreadUserMessage>()
                .lastOrNull()?.content?.filterIsInstance<TextContentPart>()
                ?.joinToString("\n") { it.text }
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
        input: ChatAgentRequest,
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