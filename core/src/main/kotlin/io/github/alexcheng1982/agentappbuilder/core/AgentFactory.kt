package io.github.alexcheng1982.agentappbuilder.core

import io.github.alexcheng1982.agentappbuilder.core.executor.AgentExecutor
import org.slf4j.LoggerFactory

object AgentFactory {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun createChatAgent(
        planner: Planner,
        name: String = "ChatAgent",
        description: String = "Conversational agent",
    ): ChatAgent {
        val executor = AgentExecutor(planner, AgentTools.agentToolWrappers)
        return ExecutableChatAgent(executor, name, description).also {
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
        planner: Planner,
        responseFactory: (Map<String, Any>) -> RESPONSE
    ): Agent<REQUEST, RESPONSE> {
        val executor = AgentExecutor(planner, AgentTools.agentToolWrappers)
        return ExecutableAgent(name, description, executor, responseFactory)
    }

    private open class ExecutableAgent<REQUEST : AgentRequest, RESPONSE>(
        private val name: String,
        private val description: String,
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
    ) : ExecutableAgent<ChatAgentRequest, ChatAgentResponse>(
        name,
        description,
        executor,
        ChatAgentResponse::fromMap
    ), ChatAgent
}