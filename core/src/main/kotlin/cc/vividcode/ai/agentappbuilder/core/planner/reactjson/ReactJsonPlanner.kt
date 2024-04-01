package cc.vividcode.ai.agentappbuilder.core.planner.reactjson

import cc.vividcode.ai.agentappbuilder.core.AgentTools
import cc.vividcode.ai.agentappbuilder.core.planner.LLMPlanner
import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

class ReactJsonPlanner(
    userPromptResource: Resource,
    systemPromptResource: Resource,
    chatClient: ChatClient
) : LLMPlanner(
    chatClient,
    PromptTemplate(userPromptResource),
    AgentTools.agentTools.values.toList(),
    ReactJsonOutputParser(),
    PromptTemplate(systemPromptResource),
) {
    companion object {
        fun createDefault(chatClient: ChatClient): ReactJsonPlanner {
            return ReactJsonPlanner(
                ClassPathResource("prompts/react-json/user.st"),
                ClassPathResource("prompts/react-json/system.st"),
                chatClient
            )
        }
    }
}