package com.javaaidev.llmagentbuilder.launcher.jdkhttpsync

import com.fasterxml.jackson.databind.ObjectMapper
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.javaaidev.llmagentbuilder.core.AgentInfoBuilder
import com.javaaidev.llmagentbuilder.core.ChatAgent
import com.javaaidev.llmagentbuilder.core.tool.AgentToolsProvider

class AgentInfoHandler(
    private val objectMapper: ObjectMapper,
    private val chatAgent: ChatAgent,
    private val agentToolsProvider: AgentToolsProvider
) : HttpHandler {

    override fun handle(exchange: HttpExchange) {
        val info = AgentInfoBuilder.info(chatAgent, agentToolsProvider)
        val json = JsonUtil.toJson(info, objectMapper)
        val data = json.toByteArray()
        exchange.sendResponseHeaders(200, data.size.toLong())
        exchange.responseBody.use {
            it.write(data)
            it.flush()
        }
    }
}