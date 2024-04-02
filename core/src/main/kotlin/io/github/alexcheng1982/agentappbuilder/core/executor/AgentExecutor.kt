package io.github.alexcheng1982.agentappbuilder.core.executor

import io.github.alexcheng1982.agentappbuilder.core.*
import io.github.alexcheng1982.agentappbuilder.core.planner.OutputParserException
import io.github.alexcheng1982.agentappbuilder.core.planner.OutputParserExceptionHandler
import io.github.alexcheng1982.agentappbuilder.core.planner.ParseResult
import org.slf4j.LoggerFactory
import org.springframework.ai.model.function.FunctionCallbackWrapper
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
    val agent: Planner,
    val nameToToolMap: Map<String, FunctionCallbackWrapper<*, *>>,
    val returnIntermediateSteps: Boolean = false,
    val maxIterations: Int? = 15,
    val maxExecutionTime: Long? = null,
    val earlyStoppingMethod: String? = "force",
    val parsingErrorHandler: OutputParserExceptionHandler? = null,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    fun call(inputs: Map<String, Any>): Map<String, Any> {
        val intermediateSteps = mutableListOf<IntermediateAgentStep>()
        var iterations = 0
        var timeElapsed = 0L
        val startTime = System.currentTimeMillis()

        while (shouldContinue(iterations, timeElapsed)) {
            val nextStepOutput =
                takeNextStep(inputs, nameToToolMap, intermediateSteps)
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
            agent.returnStoppedResponse(earlyStoppingMethod, intermediateSteps)
        return returnResult(output, intermediateSteps)
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
        nameToToolMap: Map<String, FunctionCallbackWrapper<*, *>>,
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
        nameToToolMap: Map<String, FunctionCallbackWrapper<*, *>>,
        intermediateSteps: List<IntermediateAgentStep>
    ): MutableList<Plannable> {
        val result = mutableListOf<Plannable>()
        try {
            val output = agent.plan(inputs, intermediateSteps)
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
            val text = e.llmOutput()
            var observation = parsingErrorHandler?.apply(e) ?: e.observation()
            val output = AgentAction("_Exception", observation, text)
            observation = ExceptionTool().apply(observation)
            result.add(AgentStep(output, observation))
        }
        return result
    }

    private fun performAgentAction(
        nameToToolMap: Map<String, FunctionCallbackWrapper<*, *>>,
        agentAction: AgentAction
    ): AgentStep {
        val observation =
            nameToToolMap[agentAction.tool]?.call(agentAction.toolInput)
                ?: "Invalid tool"
        return AgentStep(agentAction, observation)
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