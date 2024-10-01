package io.github.llmagentbuilder.planner.executor

import io.github.llmagentbuilder.core.AgentFinish
import io.github.llmagentbuilder.core.IntermediateAgentStep
import io.github.llmagentbuilder.core.Planner
import io.github.llmagentbuilder.core.executor.ActionPlanningResult
import io.github.llmagentbuilder.core.planner.OutputParser
import org.springframework.ai.chat.client.ChatClient

open class LLMPlanExecutor(
    private val chatClient: ChatClient,
    private val outputParser: OutputParser,
) : Planner {
    override fun plan(
        inputs: Map<String, Any>,
        intermediateSteps: List<IntermediateAgentStep>
    ): ActionPlanningResult {
        val userInput =
            (inputs["input"] as? String)
                ?: throw RuntimeException("Invalid input")
        val response = chatClient.prompt().user(userInput).call().content()
        if (response.isEmpty()) {
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

}