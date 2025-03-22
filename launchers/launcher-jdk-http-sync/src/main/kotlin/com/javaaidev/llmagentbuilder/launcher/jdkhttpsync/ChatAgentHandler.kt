package com.javaaidev.llmagentbuilder.launcher.jdkhttpsync

import com.fasterxml.jackson.databind.ObjectMapper
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.javaaidev.llmagentbuilder.core.ChatAgent
import com.javaaidev.llmagentbuilder.core.ChatAgentRequest
import org.slf4j.LoggerFactory

class ChatAgentHandler(
    private val objectMapper: ObjectMapper,
    private val chatAgent: ChatAgent,
) : HttpHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun handle(exchange: HttpExchange) {
        if (exchange.requestMethod != "POST") {
            writeResponse(exchange, 405, "Media type not allowed", "text/plain")
            return
        }

        val request = try {
            exchange.requestBody.use {
                JsonUtil.fromJson(
                    String(it.readAllBytes()),
                    ChatAgentRequest::class.java,
                    objectMapper
                )
            }
        } catch (e: Exception) {
            logger.error("Failed to parse request", e)
            writeResponse(exchange, 400, "Invalid request data", "text/plain")
            return
        }

        try {
            val response = chatAgent.call(request)
            val json =
                JsonUtil.toJson(response, objectMapper)
            writeResponse(exchange, 200, json)
        } catch (e: Exception) {
            logger.error("Failed to call agent", e)
            writeResponse(exchange, 500, "Internal error", "text/plain")
        }
    }

    private fun writeResponse(
        exchange: HttpExchange,
        code: Int,
        response: String,
        contentType: String = "application/json"
    ) {
        exchange.responseHeaders.add("Content-Type", contentType)
        val data = response.toByteArray()
        exchange.sendResponseHeaders(code, data.size.toLong())
        exchange.responseBody.use {
            it.write(data)
            it.flush()
        }
    }
}