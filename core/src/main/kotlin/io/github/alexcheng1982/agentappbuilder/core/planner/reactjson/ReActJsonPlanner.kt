package io.github.alexcheng1982.agentappbuilder.core.planner.reactjson

import io.github.alexcheng1982.agentappbuilder.core.chatmemory.ChatMemoryStore
import io.github.alexcheng1982.agentappbuilder.core.planner.LLMPlanner
import io.github.alexcheng1982.agentappbuilder.core.tool.AgentToolsProvider
import io.github.alexcheng1982.agentappbuilder.core.tool.AutoDiscoveredAgentToolsProvider
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.observation.ObservationRegistry
import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

class ReActJsonPlanner(
    chatClient: ChatClient,
    agentToolsProvider: AgentToolsProvider,
    userPromptResource: Resource,
    systemPromptResource: Resource,
    systemInstruction: String? = null,
    chatMemoryStore: ChatMemoryStore? = null,
    observationRegistry: ObservationRegistry? = null,
    meterRegistry: MeterRegistry? = null,
) : LLMPlanner(
    chatClient,
    agentToolsProvider,
    ReActJsonOutputParser(),
    PromptTemplate(userPromptResource),
    PromptTemplate(systemPromptResource),
    systemInstruction,
    chatMemoryStore,
    observationRegistry = observationRegistry,
    meterRegistry = meterRegistry,
) {
    companion object {
        fun createDefault(
            chatClient: ChatClient,
            agentToolsProvider: AgentToolsProvider = AutoDiscoveredAgentToolsProvider,
            systemInstruction: String? = null,
            chatMemoryStore: ChatMemoryStore? = null,
            observationRegistry: ObservationRegistry? = null,
            meterRegistry: MeterRegistry? = null,
        ): ReActJsonPlanner {
            return ReActJsonPlanner(
                chatClient,
                agentToolsProvider,
                ClassPathResource("prompts/react-json/user.st"),
                ClassPathResource("prompts/react-json/system.st"),
                systemInstruction,
                chatMemoryStore,
                observationRegistry,
                meterRegistry,
            )
        }
    }
}