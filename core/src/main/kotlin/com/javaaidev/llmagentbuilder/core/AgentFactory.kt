package com.javaaidev.llmagentbuilder.core

import com.javaaidev.llmagentbuilder.core.executor.AgentExecutor
import com.javaaidev.llmagentbuilder.core.tool.AgentToolWrappersProvider
import com.javaaidev.llmagentbuilder.core.tool.AgentToolsProvider
import io.micrometer.observation.ObservationRegistry
import org.slf4j.LoggerFactory

object AgentFactory {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun createChatAgent(
        planner: Planner,
        name: String? = null,
        description: String? = null,
        usageInstruction: String? = null,
        agentToolsProvider: AgentToolsProvider,
        id: String? = null,
        observationRegistry: ObservationRegistry? = null,
    ): ChatAgent {
        val agentName = name ?: "ChatAgent"
        val executor = createAgentExecutor(
            agentName,
            planner,
            agentToolsProvider,
            observationRegistry
        )
        return ExecutableChatAgent(
            executor,
            agentName,
            description ?: "A conversational chat agent",
            usageInstruction ?: "Ask me anything",
            id,
            observationRegistry,
        ).also {
            logger.info(
                "Created ChatAgent [{}] with planner [{}]",
                agentName,
                planner
            )
        }
    }

    fun <RESPONSE> create(
        name: String,
        description: String,
        usageInstruction: String,
        planner: Planner,
        responseFactory: (Map<String, Any>) -> RESPONSE,
        agentToolsProvider: AgentToolsProvider,
        id: String? = null,
        observationRegistry: ObservationRegistry? = null,
    ): Agent<ChatAgentRequest, RESPONSE> {
        val executor = createAgentExecutor(
            name,
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
            id,
            observationRegistry,
        )
    }

    private fun createAgentExecutor(
        agentName: String,
        planner: Planner,
        agentToolsProvider: AgentToolsProvider,
        observationRegistry: ObservationRegistry? = null,
    ): AgentExecutor {
        return AgentExecutor(
            agentName,
            planner,
            AgentToolWrappersProvider(
                agentToolsProvider,
                observationRegistry
            ).get(),
            observationRegistry = observationRegistry,
        )
    }

    private open class ExecutableAgent<RESPONSE>(
        private val name: String,
        private val description: String,
        private val usageInstruction: String,
        private val executor: AgentExecutor,
        private val responseFactory: (Map<String, Any>) -> RESPONSE,
        private val id: String? = null,
        private val observationRegistry: ObservationRegistry? = null,
    ) :
        Agent<ChatAgentRequest, RESPONSE> {
        private val logger = LoggerFactory.getLogger("AgentExecutor")

        override fun id(): String {
            return id ?: super.id()
        }

        override fun name(): String {
            return name
        }

        override fun description(): String {
            return description
        }

        override fun usageInstruction(): String {
            return usageInstruction
        }

        override fun call(request: ChatAgentRequest): RESPONSE {
            logger.info(
                "Start executing agent [{}] with request [{}]",
                name(),
                request
            )
            val response = responseFactory(executor.call(request))
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
        id: String? = null,
        observationRegistry: ObservationRegistry? = null,
    ) : ExecutableAgent<ChatAgentResponse>(
        name,
        description,
        usageInstruction,
        executor,
        ChatAgentResponse::fromMap,
        id,
        observationRegistry,
    ), ChatAgent
}