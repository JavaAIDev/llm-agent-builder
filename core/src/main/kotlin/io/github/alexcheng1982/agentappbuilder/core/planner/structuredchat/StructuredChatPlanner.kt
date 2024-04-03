package io.github.alexcheng1982.agentappbuilder.core.planner.structuredchat

import io.github.alexcheng1982.agentappbuilder.core.AgentTools
import io.github.alexcheng1982.agentappbuilder.core.chatmemory.ChatMemoryStore
import io.github.alexcheng1982.agentappbuilder.core.planner.LLMPlanner
import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

class StructuredChatPlanner(
    chatClient: ChatClient,
    userPromptResource: Resource,
    systemPromptResource: Resource,
    chatMemoryStore: ChatMemoryStore? = null,
) : LLMPlanner(
    chatClient,
    AgentTools.agentTools.values.toList(),
    StructuredChatOutputParser(),
    PromptTemplate(userPromptResource),
    PromptTemplate(systemPromptResource),
    chatMemoryStore = chatMemoryStore,
) {
    companion object {
        fun createDefault(chatClient: ChatClient): StructuredChatPlanner {
            return StructuredChatPlanner(
                chatClient,
                ClassPathResource("prompts/structured-chat/user.st"),
                ClassPathResource("prompts/structured-chat/system.st")
            )
        }
    }
}