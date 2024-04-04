package io.github.alexcheng1982.agentappbuilder.core.planner.structuredchat

import io.github.alexcheng1982.agentappbuilder.core.chatmemory.ChatMemoryStore
import io.github.alexcheng1982.agentappbuilder.core.planner.LLMPlanner
import io.github.alexcheng1982.agentappbuilder.core.tool.AgentToolsProvider
import io.github.alexcheng1982.agentappbuilder.core.tool.AutoDiscoveredAgentToolsProvider
import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

class StructuredChatPlanner(
    chatClient: ChatClient,
    agentToolsProvider: AgentToolsProvider,
    userPromptResource: Resource,
    systemPromptResource: Resource,
    systemInstruction: String? = null,
    chatMemoryStore: ChatMemoryStore? = null,
) : LLMPlanner(
    chatClient,
    agentToolsProvider,
    StructuredChatOutputParser(),
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
        ): StructuredChatPlanner {
            return StructuredChatPlanner(
                chatClient,
                agentToolsProvider,
                ClassPathResource("prompts/structured-chat/user.st"),
                ClassPathResource("prompts/structured-chat/system.st"),
                systemInstruction,
                chatMemoryStore,
            )
        }
    }
}