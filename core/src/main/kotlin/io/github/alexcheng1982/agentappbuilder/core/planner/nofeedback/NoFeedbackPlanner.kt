package io.github.alexcheng1982.agentappbuilder.core.planner.nofeedback

import io.github.alexcheng1982.agentappbuilder.core.chatmemory.ChatMemoryStore
import io.github.alexcheng1982.agentappbuilder.core.planner.LLMPlanner
import io.github.alexcheng1982.agentappbuilder.core.tool.AgentToolsProvider
import io.github.alexcheng1982.agentappbuilder.core.tool.AutoDiscoveredAgentToolsProvider
import io.micrometer.observation.ObservationRegistry
import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

/**
 * Planning without feedback
 */
class NoFeedbackPlanner(
    chatClient: ChatClient,
    agentToolsProvider: AgentToolsProvider,
    userPromptResource: Resource,
    systemPromptResource: Resource,
    systemInstruction: String? = null,
    chatMemoryStore: ChatMemoryStore? = null,
    observationRegistry: ObservationRegistry? = null,
) : LLMPlanner(
    chatClient,
    agentToolsProvider,
    NoFeedbackOutputParser(),
    PromptTemplate(userPromptResource),
    PromptTemplate(systemPromptResource),
    systemInstruction,
    chatMemoryStore,
    observationRegistry = observationRegistry,
) {
    companion object {
        fun createDefault(
            chatClient: ChatClient,
            agentToolsProvider: AgentToolsProvider = AutoDiscoveredAgentToolsProvider,
            systemInstruction: String? = null,
            chatMemoryStore: ChatMemoryStore? = null,
            observationRegistry: ObservationRegistry? = null,
        ): NoFeedbackPlanner {
            return NoFeedbackPlanner(
                chatClient,
                agentToolsProvider,
                ClassPathResource("prompts/no-feedback/user.st"),
                ClassPathResource("prompts/no-feedback/system.st"),
                systemInstruction,
                chatMemoryStore,
                observationRegistry,
            )
        }
    }
}