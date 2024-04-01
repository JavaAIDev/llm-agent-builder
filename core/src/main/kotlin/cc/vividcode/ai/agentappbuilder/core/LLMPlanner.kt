package cc.vividcode.ai.agentappbuilder.core

import cc.vividcode.ai.agent.dashscope.DashscopeChatClient
import cc.vividcode.ai.agent.dashscope.DashscopeChatOptions
import cc.vividcode.ai.agent.dashscope.api.DashscopeModelName
import cc.vividcode.ai.agentappbuilder.core.executor.ActionPlanningResult
import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate

open class LLMPlanner(
    private val chatClient: ChatClient,
    private val promptTemplate: PromptTemplate,
    private val tools: List<AgentTool<*, *>>,
    private val outputParser: OutputParser
) : Planner {
    override fun plan(
        inputs: Map<String, Any>,
        intermediateSteps: List<IntermediateAgentStep>
    ): ActionPlanningResult {
        val thoughts = constructScratchpad(intermediateSteps)
        val toolNames = tools.map { it.name() }
        val context = inputs + mutableMapOf(
            "agent_scratchpad" to thoughts,
            "tools" to renderTool(tools),
            "tool_names" to toolNames.joinToString(", ")
        )
        val prompt = Prompt(
            promptTemplate.createMessage(context),
            prepareChatClientOptions(chatClient, toolNames)
        )
        val response = chatClient.call(prompt)
        val text = response.result.output.content
        val result = outputParser.parse(text)
        return ActionPlanningResult.fromParseResult(result)
    }

    private fun constructScratchpad(intermediateSteps: List<IntermediateAgentStep>): String {
        return intermediateSteps.joinToString(" ") {
            val (action, observation) = it
            "${action.log} \n$observation\n"
        }
    }

    private fun renderTool(tools: List<AgentTool<*, *>>): String {
        return tools.joinToString("\n") {
            "${it.name()}: ${it.description()}"
        }
    }

    private fun prepareChatClientOptions(
        chatClient: ChatClient,
        toolNames: List<String>
    ): ChatOptions? {
        if (chatClient is DashscopeChatClient) {
            return DashscopeChatOptions.builder()
                .withModel(DashscopeModelName.QWEN_MAX)
                .withTemperature(0.2f)
                .withFunctions(toolNames.toSet())
                .build()
        }
        return null
    }
}