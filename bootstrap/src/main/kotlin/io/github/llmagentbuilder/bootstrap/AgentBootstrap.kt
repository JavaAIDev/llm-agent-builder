package io.github.llmagentbuilder.bootstrap

import io.github.llmagentbuilder.agent.profile.systemmessage.SystemMessageProfileAdvisor
import io.github.llmagentbuilder.agent.tool.AgentToolContextAdvisor
import io.github.llmagentbuilder.core.AgentFactory
import io.github.llmagentbuilder.core.ChatAgent
import io.github.llmagentbuilder.core.ChatModelProvider
import io.github.llmagentbuilder.core.PlannerProvider
import io.github.llmagentbuilder.core.tool.AgentToolFunctionCallbackContext
import io.github.llmagentbuilder.core.tool.AutoDiscoveredAgentToolsProvider
import io.github.llmagentbuilder.launcher.jdkhttpsync.JdkHttpSyncLauncher
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.chat.client.advisor.api.Advisor
import org.springframework.ai.chat.memory.InMemoryChatMemory
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import java.io.FileReader
import java.nio.file.Path
import java.util.*
import kotlin.streams.asSequence

object AgentBootstrap {
    fun bootstrap(configFile: Path) {
        JdkHttpSyncLauncher().launch(
            buildAgent(configFile)
        )
    }

    private fun buildAgent(configFile: Path): ChatAgent {
        val config =
            Yaml(
                Constructor(
                    AgentConfig::class.java,
                    LoaderOptions()
                )
            ).load<AgentConfig>(FileReader(configFile.toFile()))
        return buildAgent(config)
    }

    private fun buildAgent(agentConfig: AgentConfig): ChatAgent {
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
        val llmConfigs = agentConfig.llm
        val chatModel = ServiceLoader.load(ChatModelProvider::class.java)
            .stream()
            .map { it.get() }
            .asSequence()
            .mapNotNull {
                val config =
                    llmConfigs?.get(it.configKey()) as? Map<String, Any?>
                it.provideChatModel(functionCallbackContext, config)
            }
            .firstOrNull() ?: throw RuntimeException("No ChatModel found")
        val chatClientBuilder = ChatClient.builder(chatModel)
            .defaultAdvisors(advisors)
        val plannerConfigs = agentConfig.planner
        val planner = ServiceLoader.load(PlannerProvider::class.java)
            .stream()
            .map { it.get() }
            .asSequence()
            .mapNotNull {
                val config =
                    plannerConfigs?.get(it.configKey()) as? Map<String, Any?>
                it.providePlanner(chatClientBuilder, config)
            }
            .firstOrNull() ?: throw RuntimeException("No Planner found")
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