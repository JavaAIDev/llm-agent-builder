package io.github.llmagentbuilder.core.executor

import io.github.llmagentbuilder.core.observation.AgentExecutionObservationContext
import io.github.llmagentbuilder.core.observation.AgentExecutionObservationDocumentation
import io.github.llmagentbuilder.core.observation.DefaultAgentExecutionObservationConvention
import io.github.llmagentbuilder.core.planner.planner.OutputParserException
import io.github.llmagentbuilder.core.planner.planner.OutputParserExceptionHandler
import io.github.llmagentbuilder.core.planner.planner.ParseResult
import io.github.llmagentbuilder.core.tool.ExceptionTool
import io.github.llmagentbuilder.core.tool.InvalidTool
import io.github.llmagentbuilder.core.tool.InvalidToolInput
import io.github.llmagentbuilder.core.*
import io.micrometer.observation.ObservationRegistry
import org.slf4j.LoggerFactory
import org.springframework.ai.model.function.FunctionCallback
import java.time.Duration
import java.util.*

data class ActionPlanningResult(
    val actions: List<AgentAction>? = null,
    val finish: AgentFinish? = null,
) {
    companion object {
        fun actions(actions: List<AgentAction>) =
            ActionPlanningResult(actions = actions)

        fun finish(finish: AgentFinish) = ActionPlanningResult(finish = finish)

        fun fromParseResult(result: ParseResult): ActionPlanningResult =
            result.action?.let {
                actions(listOf(it))
            } ?: finish(result.finish ?: throw IllegalArgumentException())
    }
}

data class NextStep(
    val steps: List<IntermediateAgentStep>? = null,
    val finish: AgentFinish? = null,
) {
    fun finished() = finish != null
}

data class AgentExecutor(
    val agentName: String,
    val planner: Planner,
    val nameToToolMap: Map<String, FunctionCallback>,
    val returnIntermediateSteps: Boolean = false,
    val maxIterations: Int? = 10,
    val maxExecutionTime: Long? = null,
    val earlyStoppingMethod: String? = "force",
    val parsingErrorHandler: OutputParserExceptionHandler? = null,
    val observationRegistry: ObservationRegistry? = null,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun call(input: Map<String, Any>): Map<String, Any> {
        val action = { internalCall(input) }
        return observationRegistry?.let { registry ->
            instrumentedCall(input, action, registry)
        } ?: action.invoke()
    }

    private fun internalCall(input: Map<String, Any>): Map<String, Any> {
        val intermediateSteps = mutableListOf<IntermediateAgentStep>()
        var iterations = 0
        var timeElapsed = 0L
        val startTime = System.currentTimeMillis()

        while (shouldContinue(iterations, timeElapsed)) {
            val nextStepOutput =
                takeNextStep(input, nameToToolMap, intermediateSteps)
            if (nextStepOutput.finish != null) {
                return returnResult(nextStepOutput.finish, intermediateSteps)
            }
            nextStepOutput.steps?.run {
                intermediateSteps.addAll(this)
            }
            iterations += 1
            timeElapsed = System.currentTimeMillis() - startTime
        }
        logger.error(
            "Agent execution failed in {} iterations in {}",
            iterations,
            Duration.ofMillis(timeElapsed)
        )
        val output =
            planner.returnStoppedResponse(
                earlyStoppingMethod,
                intermediateSteps
            )
        return returnResult(output, intermediateSteps)
    }

    private fun instrumentedCall(
        input: Map<String, Any>,
        action: () -> Map<String, Any>,
        registry: ObservationRegistry
    ): Map<String, Any> {
        val observationContext =
            AgentExecutionObservationContext(agentName, input)
        val observation =
            AgentExecutionObservationDocumentation.AGENT_EXECUTION.observation(
                null,
                DefaultAgentExecutionObservationConvention(),
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

    private fun returnResult(
        agentFinish: AgentFinish,
        intermediateSteps: List<IntermediateAgentStep>
    ): Map<String, Any> {
        return agentFinish.returnValues + (if (returnIntermediateSteps) mapOf(
            "intermediateSteps" to intermediateSteps
        ) else mapOf())
    }

    private fun takeNextStep(
        inputs: Map<String, Any>,
        nameToToolMap: Map<String, FunctionCallback>,
        intermediateSteps: List<IntermediateAgentStep>
    ): NextStep {
        return consumeNextStep(
            iterateNextStep(
                inputs,
                nameToToolMap,
                intermediateSteps
            )
        )
    }

    private fun consumeNextStep(values: List<Plannable>): NextStep {
        if (values.last() is AgentFinish) {
            return NextStep(finish = values.last() as AgentFinish)
        } else {
            return NextStep(steps = values.filterIsInstance<AgentStep>()
                .map {
                    IntermediateAgentStep(
                        it.action,
                        Objects.toString(it.observation)
                    )
                })
        }
    }

    private fun iterateNextStep(
        inputs: Map<String, Any>,
        nameToToolMap: Map<String, FunctionCallback>,
        intermediateSteps: List<IntermediateAgentStep>
    ): MutableList<Plannable> {
        val result = mutableListOf<Plannable>()
        try {
            val output = planner.plan(inputs, intermediateSteps)
            if (output.finish != null) {
                result.add(output.finish)
                return result
            }
            output.actions?.forEach {
                result.add(it)
            }
            output.actions?.forEach {
                result.add(performAgentAction(nameToToolMap, it))
            }
        } catch (e: OutputParserException) {
            logger.error("Output parsing error for input {}", inputs, e)
            val text = e.llmOutput()
            var observation = parsingErrorHandler?.apply(e) ?: e.observation()
            val output = AgentAction("_Exception", observation, text)
            observation = ExceptionTool().apply(observation)
            result.add(AgentStep(output, observation))
        }
        return result
    }

    private fun performAgentAction(
        nameToToolMap: Map<String, FunctionCallback>,
        agentAction: AgentAction
    ): AgentStep {
        val agentTool = nameToToolMap[agentAction.tool]
        if (agentTool != null) {
            val (tool, toolInput) = agentAction
            logger.info(
                "Start executing tool [{}] with request [{}]",
                tool,
                toolInput
            )
            val observation = agentTool.call(toolInput)
            logger.info(
                "Tool [{}] executed with request [{}], response is [{}]",
                tool,
                toolInput,
                observation
            )
            return AgentStep(agentAction, observation)
        } else {
            val observation = InvalidTool().apply(
                InvalidToolInput(
                    agentAction.tool,
                    nameToToolMap.keys,
                )
            )
            return AgentStep(
                agentAction,
                observation,
            )
        }
    }

    private fun shouldContinue(iterations: Int, timeElapsed: Long): Boolean {
        if (maxIterations != null && iterations >= maxIterations) {
            return false
        }
        if (maxExecutionTime != null && timeElapsed >= maxExecutionTime) {
            return false
        }
        return true
    }
}