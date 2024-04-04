package io.github.alexcheng1982.agentappbuilder.core.planner.react

import io.github.alexcheng1982.agentappbuilder.core.chatmemory.ChatMemoryStore
import io.github.alexcheng1982.agentappbuilder.core.planner.LLMPlanner
import io.github.alexcheng1982.agentappbuilder.core.tool.AgentToolsProvider
import io.github.alexcheng1982.agentappbuilder.core.tool.AutoDiscoveredAgentToolsProvider
import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

class ReActPlanner(
    chatClient: ChatClient,
    agentToolsProvider: AgentToolsProvider,
    userPromptResource: Resource,
    systemPromptResource: Resource,
    systemInstruction: String? = null,
    chatMemoryStore: ChatMemoryStore? = null,
) :
    LLMPlanner(
        chatClient,
        agentToolsProvider,
        ReActOutputParser(),
        PromptTemplate(userPromptResource),
        PromptTemplate(systemPromptResource),
        systemInstruction,
        chatMemoryStore,
    ) {
    companion object {
        fun createDefault(
            chatClient: ChatClient,
            agentToolsProvider: AgentToolsProvider = AutoDiscoveredAgentToolsProvider,
            systemInstruction: String? = null,
            chatMemoryStore: ChatMemoryStore? = null,
        ): ReActPlanner {
            return ReActPlanner(
                chatClient,
                agentToolsProvider,
                ClassPathResource("prompts/react/user.st"),
                ClassPathResource("prompts/react/system.st"),
                systemInstruction,
                chatMemoryStore,
            )
        }
    }
}