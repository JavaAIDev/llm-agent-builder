package io.github.llmagentbuilder.bootstrap

import io.github.llmagentbuilder.agent.profile.systemmessage.SystemMessageProfileAdvisor
import io.github.llmagentbuilder.agent.tool.AgentToolInfoAdvisor
import io.github.llmagentbuilder.core.*
import io.github.llmagentbuilder.core.tool.AgentToolFunctionCallbackContext
import io.github.llmagentbuilder.core.tool.AgentToolsProviderFactory
import io.github.llmagentbuilder.launcher.ktor.server.KtorLauncher
import io.github.llmagentbuilder.plugin.observation.opentelemetry.OpenTelemetryPlugin
import io.micrometer.observation.ObservationRegistry
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.chat.client.advisor.api.Advisor
import org.springframework.ai.chat.memory.InMemoryChatMemory
import java.io.InputStream
import java.nio.file.Path
import java.util.*
import kotlin.streams.asSequence

object AgentBootstrap {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun bootstrap(configFile: Path) {
        bootstrap(AgentConfigLoader.load(configFile.toFile()))
    }

    fun bootstrap(configFileStream: InputStream) {
        bootstrap(AgentConfigLoader.load(configFileStream))
    }

    fun bootstrap(agentConfig: AgentConfig) {
        val advisors = listOf(
            this::profileAdvisor,
            this::inMemoryMessageHistoryAdvisor
        ).mapNotNull {
            it(agentConfig)
        }.toMutableList()
        val agentToolsProvider =
            AgentToolsProviderFactory.create(agentConfig.tools ?: listOf())
        advisors.addLast(
            AgentToolInfoAdvisor(
                agentToolsProvider.get()
            )
        )
        advisors.addLast(SimpleLoggerAdvisor())
        val observationEnabled = agentConfig.observation?.enabled == true
        val observationRegistry =
            if (observationEnabled) ObservationRegistry.create() else ObservationRegistry.NOOP
        val functionCallbackContext =
            AgentToolFunctionCallbackContext(
                agentToolsProvider,
                observationRegistry,
            )
        if (observationEnabled) {
            OpenTelemetryPlugin().install(agentConfig, observationRegistry)
        }
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
            .firstOrNull()?.also {
                logger.info("Loaded ChatModel $it")
            } ?: throw RuntimeException("No ChatModel found")
        val chatClientBuilder =
            ChatClient.builder(chatModel, observationRegistry, null)
                .defaultAdvisors(advisors)
        val plannerConfigs = agentConfig.planner
        val planner = ServiceLoader.load(PlannerProvider::class.java)
            .stream()
            .map { it.get() }
            .asSequence()
            .mapNotNull {
                val config =
                    plannerConfigs?.get(it.configKey()) as? Map<String, Any?>
                it.providePlanner(
                    chatClientBuilder,
                    config,
                    observationRegistry,
                )
            }
            .firstOrNull()?.also {
                logger.info("Loaded Planner $it")
            } ?: throw RuntimeException("No Planner found")
        val metadata = agentConfig.metadata
        val chatAgent = AgentFactory.createChatAgent(
            planner,
            metadata?.name,
            metadata?.description,
            metadata?.usageInstruction,
            agentToolsProvider,
            UUID.randomUUID().toString(),
            observationRegistry,
        )
        KtorLauncher.launch(chatAgent)
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