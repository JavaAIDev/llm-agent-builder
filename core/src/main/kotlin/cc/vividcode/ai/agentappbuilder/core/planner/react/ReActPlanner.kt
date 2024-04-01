package cc.vividcode.ai.agentappbuilder.core.planner.react

import cc.vividcode.ai.agentappbuilder.core.AgentTools
import cc.vividcode.ai.agentappbuilder.core.LLMPlanner
import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

class ReActPlanner(promptResource: Resource, chatClient: ChatClient) :
    LLMPlanner(
        chatClient,
        PromptTemplate(promptResource),
        AgentTools.agentTools.values.toList(),
        ReActOutputParser()
    ) {
    companion object {
        fun createDefault(chatClient: ChatClient): ReActPlanner {
            val promptResource = ClassPathResource("prompts/react/user.st")
            return ReActPlanner(promptResource, chatClient)
        }
    }
}