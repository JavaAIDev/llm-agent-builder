package io.github.alexcheng1982.agentappbuilder.core.planner.reactjson

import io.github.alexcheng1982.agentappbuilder.core.AgentTools
import io.github.alexcheng1982.agentappbuilder.core.chatmemory.ChatMemoryStore
import io.github.alexcheng1982.agentappbuilder.core.planner.LLMPlanner
import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

class ReactJsonPlanner(
    userPromptResource: Resource,
    systemPromptResource: Resource,
    chatClient: ChatClient,
    systemInstruction: String? = null,
    chatMemoryStore: ChatMemoryStore? = null,
) : LLMPlanner(
    chatClient,
    PromptTemplate(userPromptResource),
    AgentTools.agentTools.values.toList(),
    ReactJsonOutputParser(),
    PromptTemplate(systemPromptResource),
    systemInstruction,
    chatMemoryStore,
) {
    companion object {
        fun createDefault(
            chatClient: ChatClient,
            systemInstruction: String? = null,
            chatMemoryStore: ChatMemoryStore? = null,
        ): ReactJsonPlanner {
            return ReactJsonPlanner(
                ClassPathResource("prompts/react-json/user.st"),
                ClassPathResource("prompts/react-json/system.st"),
                chatClient,
                systemInstruction,
                chatMemoryStore,
            )
        }
    }
}