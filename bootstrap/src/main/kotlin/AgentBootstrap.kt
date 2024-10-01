import io.github.llmagentbuilder.agent.profile.systemmessage.SystemMessageProfileAdvisor
import io.github.llmagentbuilder.agent.tool.AgentToolContextAdvisor
import io.github.llmagentbuilder.core.AgentFactory
import io.github.llmagentbuilder.core.ChatAgent
import io.github.llmagentbuilder.core.ChatModelProvider
import io.github.llmagentbuilder.core.PlannerProvider
import io.github.llmagentbuilder.core.tool.AgentToolFunctionCallbackContext
import io.github.llmagentbuilder.core.tool.AutoDiscoveredAgentToolsProvider
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
        advisors.addLast(SimpleLoggerAdvisor())
        val functionCallbackContext =
            AgentToolFunctionCallbackContext(
                AutoDiscoveredAgentToolsProvider,
            )
        val chatModel = ServiceLoader.load(ChatModelProvider::class.java)
            .stream()
            .findFirst()
            .map { it.get() }
            .map { it.provideChatModel(functionCallbackContext) }
            .orElseThrow { RuntimeException("No ChatModel found") }
        val chatClientBuilder = ChatClient.builder(chatModel)
            .defaultAdvisors(advisors)
        val planner = ServiceLoader.load(PlannerProvider::class.java)
            .stream()
            .findFirst()
            .map { it.get() }
            .map { it.providePlanner(chatClientBuilder) }
            .orElseThrow { RuntimeException("No Planner found") }
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