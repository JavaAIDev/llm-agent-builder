package io.github.alexcheng1982.agentappbuilder.core

import io.github.alexcheng1982.agentappbuilder.core.executor.AgentExecutor
import io.github.alexcheng1982.agentappbuilder.core.tool.AgentToolWrappersProvider
import io.github.alexcheng1982.agentappbuilder.core.tool.AgentToolsProvider
import io.github.alexcheng1982.agentappbuilder.core.tool.AutoDiscoveredAgentToolsProvider
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import org.slf4j.LoggerFactory

object AgentFactory {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun createChatAgent(
        planner: Planner,
        name: String = "ChatAgent",
        description: String = "A conversational chat agent",
        usageInstruction: String = "Ask me anything",
        agentToolsProvider: AgentToolsProvider = AutoDiscoveredAgentToolsProvider,
        observationRegistry: ObservationRegistry? = null,
    ): ChatAgent {
        val executor = createAgentExecutor(
            planner,
            agentToolsProvider,
            observationRegistry
        )
        return ExecutableChatAgent(
            executor,
            name,
            description,
            usageInstruction,
            observationRegistry,
        ).also {
            logger.info(
                "Created ChatAgent [{}] with planner [{}]",
                name,
                planner
            )
        }
    }

    fun <REQUEST : AgentRequest, RESPONSE> create(
        name: String,
        description: String,
        usageInstruction: String,
        planner: Planner,
        responseFactory: (Map<String, Any>) -> RESPONSE,
        agentToolsProvider: AgentToolsProvider = AutoDiscoveredAgentToolsProvider,
        observationRegistry: ObservationRegistry? = null,
    ): Agent<REQUEST, RESPONSE> {
        val executor = createAgentExecutor(
            planner,
            agentToolsProvider,
            observationRegistry
        )
        return ExecutableAgent(
            name,
            description,
            usageInstruction,
            executor,
            responseFactory,
            observationRegistry,
        )
    }

    private fun createAgentExecutor(
        planner: Planner,
        agentToolsProvider: AgentToolsProvider,
        observationRegistry: ObservationRegistry? = null,
    ): AgentExecutor {
        return AgentExecutor(
            planner,
            AgentToolWrappersProvider(
                agentToolsProvider,
                observationRegistry
            ).get(),
            observationRegistry = observationRegistry,
        )
    }

    private open class ExecutableAgent<REQUEST : AgentRequest, RESPONSE>(
        private val name: String,
        private val description: String,
        private val usageInstruction: String,
        private val executor: AgentExecutor,
        private val responseFactory: (Map<String, Any>) -> RESPONSE,
        private val observationRegistry: ObservationRegistry? = null,
    ) :
        Agent<REQUEST, RESPONSE> {
        private val logger = LoggerFactory.getLogger(javaClass)
        override fun name(): String {
            return name
        }

        override fun description(): String {
            return description
        }

        override fun usageInstruction(): String {
            return usageInstruction
        }

        override fun call(request: REQUEST): RESPONSE {
            logger.info(
                "Start executing agent [{}] with request [{}]",
                name(),
                request
            )
            val action = { responseFactory(executor.call(request.toMap())) }
            val response = observationRegistry?.let { registry ->
                Observation.createNotStarted("agent.execution", registry)
                    .lowCardinalityKeyValue("agent.name", name())
                    .observe(action)
            } ?: action.invoke()
            logger.info(
                "Finished executing agent [{}] with response [{}]",
                name(),
                response
            )
            return response
        }

    }

    private class ExecutableChatAgent(
        executor: AgentExecutor,
        name: String,
        description: String,
        usageInstruction: String,
        observationRegistry: ObservationRegistry? = null,
    ) : ExecutableAgent<ChatAgentRequest, ChatAgentResponse>(
        name,
        description,
        usageInstruction,
        executor,
        ChatAgentResponse::fromMap,
        observationRegistry,
    ), ChatAgent
}