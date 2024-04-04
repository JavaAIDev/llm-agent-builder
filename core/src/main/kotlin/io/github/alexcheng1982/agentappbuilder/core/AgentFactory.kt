package io.github.alexcheng1982.agentappbuilder.core

import io.github.alexcheng1982.agentappbuilder.core.executor.AgentExecutor
import io.github.alexcheng1982.agentappbuilder.core.tool.AgentToolWrappersProvider
import io.github.alexcheng1982.agentappbuilder.core.tool.AgentToolsProvider
import io.github.alexcheng1982.agentappbuilder.core.tool.AutoDiscoveredAgentToolsProvider
import org.slf4j.LoggerFactory

object AgentFactory {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun createChatAgent(
        planner: Planner,
        name: String = "ChatAgent",
        description: String = "A conversational chat agent",
        usageInstruction: String = "Ask me anything",
        agentToolsProvider: AgentToolsProvider = AutoDiscoveredAgentToolsProvider,
    ): ChatAgent {
        val executor = createAgentExecutor(planner, agentToolsProvider)
        return ExecutableChatAgent(
            executor,
            name,
            description,
            usageInstruction
        ).also {
            logger.info(
                "Created ChatAgent [{}] with planner [{}]",
                name,
                planner.javaClass.simpleName
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
    ): Agent<REQUEST, RESPONSE> {
        val executor = createAgentExecutor(planner, agentToolsProvider)
        return ExecutableAgent(
            name,
            description,
            usageInstruction,
            executor,
            responseFactory
        )
    }

    private fun createAgentExecutor(
        planner: Planner,
        agentToolsProvider: AgentToolsProvider
    ): AgentExecutor {
        return AgentExecutor(
            planner,
            AgentToolWrappersProvider(agentToolsProvider).get()
        )
    }

    private open class ExecutableAgent<REQUEST : AgentRequest, RESPONSE>(
        private val name: String,
        private val description: String,
        private val usageInstruction: String,
        private val executor: AgentExecutor,
        private val responseFactory: (Map<String, Any>) -> RESPONSE,
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
            return responseFactory(executor.call(request.toMap())).also {
                logger.info(
                    "Finished executing agent [{}] with response [{}]",
                    name(),
                    it
                )
            }
        }

    }

    private class ExecutableChatAgent(
        executor: AgentExecutor,
        name: String,
        description: String,
        usageInstruction: String,
    ) : ExecutableAgent<ChatAgentRequest, ChatAgentResponse>(
        name,
        description,
        usageInstruction,
        executor,
        ChatAgentResponse::fromMap
    ), ChatAgent
}