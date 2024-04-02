package io.github.alexcheng1982.agentappbuilder.core.planner.structuredchat

import io.github.alexcheng1982.agentappbuilder.core.AgentTools
import io.github.alexcheng1982.agentappbuilder.core.planner.LLMPlanner
import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

class StructuredChatPlanner(
    userPromptResource: Resource,
    systemPromptResource: Resource,
    chatClient: ChatClient
) : LLMPlanner(
    chatClient,
    PromptTemplate(userPromptResource),
    AgentTools.agentTools.values.toList(),
    StructuredChatOutputParser(),
    PromptTemplate(systemPromptResource),
) {
    companion object {
        fun createDefault(chatClient: ChatClient): StructuredChatPlanner {
            return StructuredChatPlanner(
                ClassPathResource("prompts/structured-chat/user.st"),
                ClassPathResource("prompts/structured-chat/system.st"),
                chatClient
            )
        }
    }
}