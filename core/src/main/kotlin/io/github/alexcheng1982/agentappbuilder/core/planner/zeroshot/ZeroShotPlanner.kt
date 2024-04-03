package io.github.alexcheng1982.agentappbuilder.core.planner.zeroshot

import io.github.alexcheng1982.agentappbuilder.core.AgentTools
import io.github.alexcheng1982.agentappbuilder.core.chatmemory.ChatMemoryStore
import io.github.alexcheng1982.agentappbuilder.core.planner.LLMPlanner
import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

class ZeroShotPlanner(
    chatClient: ChatClient,
    userPromptResource: Resource,
    systemPromptResource: Resource,
    systemInstruction: String? = null,
    chatMemoryStore: ChatMemoryStore? = null,
) : LLMPlanner(
    chatClient, AgentTools.agentTools.values.toList(),
    ZeroShotOutputParser(),
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
        ): ZeroShotPlanner {
            return ZeroShotPlanner(
                chatClient,
                ClassPathResource("prompts/zero-shot/user.st"),
                ClassPathResource("prompts/zero-shot/system.st"),
                systemInstruction,
                chatMemoryStore,
            )
        }
    }
}