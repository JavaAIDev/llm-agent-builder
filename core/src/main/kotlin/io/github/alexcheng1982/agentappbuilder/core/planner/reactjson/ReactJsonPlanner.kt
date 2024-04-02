package io.github.alexcheng1982.agentappbuilder.core.planner.reactjson

import io.github.alexcheng1982.agentappbuilder.core.AgentTools
import io.github.alexcheng1982.agentappbuilder.core.chatmemory.ChatMemory
import io.github.alexcheng1982.agentappbuilder.core.planner.LLMPlanner
import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

class ReactJsonPlanner(
    userPromptResource: Resource,
    systemPromptResource: Resource,
    chatClient: ChatClient,
    chatMemory: ChatMemory? = null
) : LLMPlanner(
    chatClient,
    PromptTemplate(userPromptResource),
    AgentTools.agentTools.values.toList(),
    ReactJsonOutputParser(),
    PromptTemplate(systemPromptResource),
    chatMemory,
) {
    companion object {
        fun createDefault(
            chatClient: ChatClient,
            chatMemory: ChatMemory? = null,
            userPromptResource: Resource = ClassPathResource("prompts/react-json/user.st"),
            systemPromptResource: Resource = ClassPathResource("prompts/react-json/system.st"),
        ): ReactJsonPlanner {
            return ReactJsonPlanner(
                userPromptResource,
                systemPromptResource,
                chatClient,
                chatMemory = chatMemory,
            )
        }
    }
}