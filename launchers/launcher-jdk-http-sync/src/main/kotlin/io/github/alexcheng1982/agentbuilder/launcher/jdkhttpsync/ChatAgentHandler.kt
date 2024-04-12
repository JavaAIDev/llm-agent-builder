package io.github.alexcheng1982.agentbuilder.launcher.jdkhttpsync

import com.fasterxml.jackson.databind.ObjectMapper
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import io.github.alexcheng1982.agentappbuilder.core.ChatAgent
import io.github.alexcheng1982.agentappbuilder.core.ChatAgentRequest

class ChatAgentHandler(
    private val objectMapper: ObjectMapper,
    private val chatAgent: ChatAgent,
) : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        try {
            val request = exchange.requestBody.use {
                JsonUtil.fromJson(
                    String(it.readAllBytes()),
                    ChatAgentRequest::class.java,
                    objectMapper
                )
            }
            try {
                val response = chatAgent.call(request)
                val json = JsonUtil.toJson(response, objectMapper)
                writeResponse(exchange, 200, json)
            } catch (e: Exception) {
                writeResponse(exchange, 500, "Internal error")
            }
        } catch (e: Exception) {
            writeResponse(exchange, 200, "Invalid request data")
        }
    }

    private fun writeResponse(exchange: HttpExchange, code: Int, response: String) {
        exchange.sendResponseHeaders(code, response.length.toLong())
        exchange.responseBody.use {
            it.write(response.toByteArray())
            it.flush()
        }
    }
}