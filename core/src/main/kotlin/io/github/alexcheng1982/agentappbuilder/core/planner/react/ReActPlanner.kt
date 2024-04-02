package io.github.alexcheng1982.agentappbuilder.core.planner.react

import io.github.alexcheng1982.agentappbuilder.core.AgentTools
import io.github.alexcheng1982.agentappbuilder.core.chatmemory.ChatMemoryStore
import io.github.alexcheng1982.agentappbuilder.core.planner.LLMPlanner
import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

class ReActPlanner(
    promptResource: Resource,
    chatClient: ChatClient,
    chatMemoryStore: ChatMemoryStore? = null,
) :
    LLMPlanner(
        chatClient,
        PromptTemplate(promptResource),
        AgentTools.agentTools.values.toList(),
        ReActOutputParser(),
        chatMemoryStore = chatMemoryStore
    ) {
    companion object {
        fun createDefault(chatClient: ChatClient): ReActPlanner {
            val promptResource = ClassPathResource("prompts/react/user.st")
            return ReActPlanner(promptResource, chatClient)
        }
    }
}