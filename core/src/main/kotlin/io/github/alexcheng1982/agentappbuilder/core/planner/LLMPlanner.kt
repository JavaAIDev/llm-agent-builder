package io.github.alexcheng1982.agentappbuilder.core.planner

import cc.vividcode.ai.agent.dashscope.DashscopeChatClient
import cc.vividcode.ai.agent.dashscope.DashscopeChatOptions
import cc.vividcode.ai.agent.dashscope.api.DashscopeModelName
import io.github.alexcheng1982.agentappbuilder.core.AgentTool
import io.github.alexcheng1982.agentappbuilder.core.IntermediateAgentStep
import io.github.alexcheng1982.agentappbuilder.core.Planner
import io.github.alexcheng1982.agentappbuilder.core.chatmemory.ChatMemory
import io.github.alexcheng1982.agentappbuilder.core.chatmemory.ChatMemoryStore
import io.github.alexcheng1982.agentappbuilder.core.chatmemory.MessageWindowChatMemory
import io.github.alexcheng1982.agentappbuilder.core.executor.ActionPlanningResult
import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate

open class LLMPlanner(
    private val chatClient: ChatClient,
    private val tools: List<AgentTool<*, *>>,
    private val outputParser: OutputParser,
    private val userPromptTemplate: PromptTemplate,
    private val systemPromptTemplate: PromptTemplate? = null,
    private val systemInstruction: String? = null,
    private val chatMemoryStore: ChatMemoryStore? = null,
    private val chatMemoryProvider: ((ChatMemoryStore, Map<String, Any>) -> ChatMemory?)? = { store, inputs ->
        inputs["memory_id"]?.let { memoryId ->
            MessageWindowChatMemory(store, memoryId.toString(), 10)
        }
    },
) : Planner {
    override fun plan(
        inputs: Map<String, Any>,
        intermediateSteps: List<IntermediateAgentStep>
    ): ActionPlanningResult {
        val systemInstruction = systemInstruction
            ?: "Answer the following questions as best you can."
        val thoughts = constructScratchpad(intermediateSteps)
        val toolNames = tools.map { it.name() }
        val context = inputs + mutableMapOf(
            "system_instruction" to systemInstruction,
            "agent_scratchpad" to thoughts,
            "tools" to renderTool(tools),
            "tool_names" to toolNames.joinToString(", ")
        )
        val messages = mutableListOf(userPromptTemplate.createMessage(context))
        systemPromptTemplate?.run {
            messages.addFirst(SystemMessage(systemPromptTemplate.render(context)))
        }

        val chatMemory = chatMemoryStore?.let { store ->
            chatMemoryProvider?.invoke(store, inputs)
        }

        chatMemory?.let { memory ->
            messages.forEach(memory::add)
        }
        val prompt = Prompt(
            chatMemory?.messages() ?: messages,
            prepareChatClientOptions(chatClient, toolNames)
        )
        val response = chatClient.call(prompt)

        chatMemory?.add(response.result.output)

        val text = response.result.output.content
        val result = outputParser.parse(text)
        return ActionPlanningResult.fromParseResult(result)
    }

    private fun constructScratchpad(intermediateSteps: List<IntermediateAgentStep>): String {
        return intermediateSteps.joinToString(" ") {
            val (action, observation) = it
            "${action.log} \nObservation: $observation\n"
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