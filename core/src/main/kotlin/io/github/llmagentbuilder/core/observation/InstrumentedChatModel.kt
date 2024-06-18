package io.github.llmagentbuilder.core.observation

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.observation.ObservationRegistry
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.ai.chat.prompt.Prompt

class InstrumentedChatModel(
    private val chatModel: ChatModel,
    private val observationRegistry: ObservationRegistry? = null,
    private val meterRegistry: MeterRegistry? = null,
) : ChatModel {
    private val objectMapper =
        ObjectMapper().registerModules(JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
    private val logger = LoggerFactory.getLogger("chatClient.debugger")


    override fun call(prompt: Prompt): ChatResponse {
        val action = { chatModel.call(prompt) }
        val response = observationRegistry?.let { registry ->
            instrumentedCall(prompt, action, registry)
        } ?: action.invoke()
        meterRegistry?.run {
            updateMetrics(response, this)
        }
        return response
    }

    override fun getDefaultOptions(): ChatOptions {
        return chatModel.defaultOptions
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
            =================
            $type:
            $json
            =================
            """.trimIndent()
        logger.debug(message)
    }
}