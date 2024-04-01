package cc.vividcode.ai.agentappbuilder.core.planner.structuredchat

import cc.vividcode.ai.agentappbuilder.core.AgentTools
import cc.vividcode.ai.agentappbuilder.core.LLMPlanner
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