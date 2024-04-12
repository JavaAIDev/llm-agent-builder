package io.github.alexcheng1982.agentbuilder.launcher.jdkhttpsync

import com.fasterxml.jackson.databind.ObjectMapper
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import io.github.alexcheng1982.agentappbuilder.core.AgentInfoBuilder
import io.github.alexcheng1982.agentappbuilder.core.ChatAgent
import io.github.alexcheng1982.agentappbuilder.core.tool.AgentToolsProvider

class AgentInfoHandler(
    private val objectMapper: ObjectMapper,
    private val chatAgent: ChatAgent,
    private val agentToolsProvider: AgentToolsProvider
) : HttpHandler {

    override fun handle(exchange: HttpExchange) {
        val info = AgentInfoBuilder.info(chatAgent, agentToolsProvider)
        val json = JsonUtil.toJson(info, objectMapper)
        exchange.sendResponseHeaders(200, json.length.toLong())
        exchange.responseBody.use {
            it.write(json.toByteArray())
            it.flush()
        }
    }
}