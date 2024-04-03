package io.github.alexcheng1982.agentappbuilder.core.planner.nofeedback

import io.github.alexcheng1982.agentappbuilder.core.AgentTools
import io.github.alexcheng1982.agentappbuilder.core.chatmemory.ChatMemoryStore
import io.github.alexcheng1982.agentappbuilder.core.planner.LLMPlanner
import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

/**
 * Planning without feedback
 */
class NoFeedbackPlanner(
    chatClient: ChatClient,
    userPromptResource: Resource,
    systemPromptResource: Resource,
    systemInstruction: String? = null,
    chatMemoryStore: ChatMemoryStore? = null,
) : LLMPlanner(
    chatClient,
    AgentTools.agentTools.values.toList(),
    NoFeedbackOutputParser(),
    PromptTemplate(userPromptResource),
    PromptTemplate(systemPromptResource),
    systemInstruction,
    chatMemoryStore,
) {
    companion object {
        fun createDefault(
            chatClient: ChatClient,
            systemInstruction: String? = null,
            chatMemoryStore: ChatMemoryStore? = null,
        ): NoFeedbackPlanner {
            return NoFeedbackPlanner(
                chatClient,
                ClassPathResource("prompts/no-feedback/user.st"),
                ClassPathResource("prompts/no-feedback/system.st"),
                systemInstruction,
                chatMemoryStore,
            )
        }
    }
}