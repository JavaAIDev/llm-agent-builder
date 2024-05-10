package io.github.llmagentbuilder.core.observation

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.observation.ObservationRegistry
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.ChatResponse
import org.springframework.ai.chat.prompt.Prompt

class InstrumentedChatClient(
    private val chatClient: ChatClient,
    private val observationRegistry: ObservationRegistry? = null,
    private val meterRegistry: MeterRegistry? = null,
) : ChatClient {
    private val objectMapper =
        ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true)
    private val logger = LoggerFactory.getLogger("chatClient.debugger")

    override fun call(prompt: Prompt): ChatResponse {
        val action = { chatClient.call(prompt) }
        val response = observationRegistry?.let { registry ->
            instrumentedCall(prompt, action, registry)
        } ?: action.invoke()
        meterRegistry?.run {
            updateMetrics(response, this)
        }
        return response
    }

    private fun instrumentedCall(
        prompt: Prompt,
        action: () -> ChatResponse,
        registry: ObservationRegistry
    ): ChatResponse {
        val observationContext =
            ChatClientRequestObservationContext(prompt)
        val observation =
            ChatClientObservationDocumentation.CHAT_CLIENT_CALL.observation(
                null,
                DefaultChatClientObservationConvention(),
                { observationContext },
                registry
            ).start()
        return try {
            observation.openScope().use {
                debugJson("Prompt", prompt)
                val response = action.invoke()
                debugJson("Response", response)
                observationContext.setResponse(response)
                response
            }
        } catch (e: Exception) {
            observation.error(e)
            throw e
        } finally {
            observation.stop()
        }
    }

    private fun updateMetrics(
        chatResponse: ChatResponse,
        registry: MeterRegistry,
    ) {
        chatResponse.metadata?.usage?.run {
            listOf(
                "prompt" to { this.promptTokens ?: 0 },
                "generation" to { this.generationTokens ?: 0 },
                "total" to { this.totalTokens ?: 0 },
            ).forEach { (name, provider) ->
                val counter = Counter.builder("agent.$name.tokens.count")
                    .register(registry)
                counter.increment(provider.invoke().toDouble())
            }
        }
    }

    private fun debugJson(type: String, input: Any) {
        if (!logger.isDebugEnabled) {
            return
        }
        val json = try {
            objectMapper.writeValueAsString(input)
        } catch (e: Exception) {
            input.toString()
        }
        val message =
            """
            ===== $type =====
            
            $json
            =================
            """.trimIndent()
        logger.debug(message)
    }
}