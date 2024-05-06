package io.github.llmagentbuilder.launcher.jdkhttpsync

import com.fasterxml.jackson.databind.ObjectMapper
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import io.github.llmagentbuilder.core.ChatAgent
import io.github.llmagentbuilder.core.ChatAgentRequest
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
                logger.error("Failed to call agent", e)
                writeResponse(exchange, 500, "Internal error", "text/plain")
            }
        } catch (e: Exception) {
            logger.error("Failed to parse request", e)
            writeResponse(exchange, 400, "Invalid request data", "text/plain")
        }
    }

    private fun writeResponse(
        exchange: HttpExchange,
        code: Int,
        response: String,
        contentType: String = "application/json"
    ) {
        exchange.responseHeaders.add("Content-Type", contentType)
        exchange.sendResponseHeaders(code, response.length.toLong())
        exchange.responseBody.use {
            it.write(response.toByteArray())
            it.flush()
        }
    }
}