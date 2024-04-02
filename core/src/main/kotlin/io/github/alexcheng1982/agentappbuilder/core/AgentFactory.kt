package io.github.alexcheng1982.agentappbuilder.core

import io.github.alexcheng1982.agentappbuilder.core.executor.AgentExecutor
import org.slf4j.LoggerFactory

object AgentFactory {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun createChatAgent(
        name: String,
        description: String,
        planner: Planner
    ): Agent<ChatAgentRequest, ChatAgentResponse> {
        return create(
            name,
            description,
            planner,
            ChatAgentResponse::fromMap
        )
    }

    fun <REQUEST : AgentRequest, RESPONSE> create(
        name: String,
        description: String,
        planner: Planner,
        responseFactory: (Map<String, Any>) -> RESPONSE
    ): Agent<REQUEST, RESPONSE> {
        val executor = AgentExecutor(planner, AgentTools.agentToolWrappers)
        return object : Agent<REQUEST, RESPONSE> {
            override fun name() = name

            override fun description() = description

            override fun call(request: REQUEST): RESPONSE {
                logger.info(
                    "Start executing agent {} with request {}",
                    name(),
                    request
                )
                return responseFactory(executor.call(request.toMap())).also {
                    logger.info(
                        "Finished executing agent {} with response {}",
                        name(),
                        it
                    )
                }
            }
        }
    }
}