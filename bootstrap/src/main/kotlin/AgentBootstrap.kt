import io.github.llmagentbuilder.agent.profile.systemmessage.SystemMessageProfileAdvisor
import io.github.llmagentbuilder.agent.tool.AgentToolContextAdvisor
import io.github.llmagentbuilder.core.AgentFactory
import io.github.llmagentbuilder.core.ChatAgent
import io.github.llmagentbuilder.core.ChatModelProvider
import io.github.llmagentbuilder.core.tool.AgentToolFunctionCallbackContext
import io.github.llmagentbuilder.core.tool.AutoDiscoveredAgentToolsProvider
import io.github.llmagentbuilder.planner.reactjson.ReActJsonPlanner
import io.github.llmagentbuilder.planner.reactjson.ReActJsonPromptAdvisor
import io.micrometer.observation.ObservationRegistry
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.chat.client.advisor.api.Advisor
import org.springframework.ai.chat.memory.InMemoryChatMemory
import java.util.*

object AgentBootstrap {
    fun boostrap(agentConfig: AgentConfig): ChatAgent {
        val advisors = listOf(
            this::profileAdvisor,
            this::inMemoryMessageHistoryAdvisor
        ).mapNotNull {
            it(agentConfig)
        }.toMutableList()
        advisors.addLast(
            AgentToolContextAdvisor(
                AutoDiscoveredAgentToolsProvider.get()
            )
        )
        val observationRegistry = ObservationRegistry.create()
        advisors.addLast(ReActJsonPromptAdvisor())
        advisors.addLast(SimpleLoggerAdvisor())
        val functionCallbackContext =
            AgentToolFunctionCallbackContext(
                AutoDiscoveredAgentToolsProvider,
                observationRegistry
            )
        val chatModel = ServiceLoader.load(ChatModelProvider::class.java)
            .stream()
            .map { it.get() }
            .findFirst()
            .map { it.provideChatModel(functionCallbackContext) }
            .orElseThrow { RuntimeException("No ChatModel found") }
        val chatClient = ChatClient.builder(chatModel)
            .defaultAdvisors(advisors)
            .build()
        val planner = ReActJsonPlanner(chatClient)
        return AgentFactory.createChatAgent(
            planner
        )
    }

    private fun profileAdvisor(agentConfig: AgentConfig): Advisor? {
        return agentConfig.profile?.system?.run {
            SystemMessageProfileAdvisor(this)
        }
    }

    private fun inMemoryMessageHistoryAdvisor(agentConfig: AgentConfig): Advisor? {
        return if (agentConfig.memory?.inMemory?.enabled == true) {
            MessageChatMemoryAdvisor(InMemoryChatMemory())
        } else null
    }
}