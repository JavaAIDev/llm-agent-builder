package io.github.alexcheng1982.agentappbuilder.core.observation

import io.micrometer.common.KeyValue
import io.micrometer.common.KeyValues
import io.micrometer.common.docs.KeyName
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationConvention
import io.micrometer.observation.ObservationRegistry
import io.micrometer.observation.docs.ObservationDocumentation
import io.micrometer.observation.transport.RequestReplySenderContext
import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.ChatResponse
import org.springframework.ai.chat.prompt.Prompt

enum class ChatClientObservationDocumentation : ObservationDocumentation {
    CHAT_CLIENT_CALL {
        override fun getDefaultConvention(): Class<out ObservationConvention<out Observation.Context>>? {
            return DefaultChatClientObservationConvention::class.java
        }

        override fun getLowCardinalityKeyNames(): Array<KeyName> {
            return LowCardinalityKeyNames.values().toList().toTypedArray()
        }

        override fun getHighCardinalityKeyNames(): Array<KeyName> {
            return HighCardinalityKeyNames.values().toList().toTypedArray()
        }
    };

    enum class LowCardinalityKeyNames : KeyName

    enum class HighCardinalityKeyNames : KeyName {
        PROMPT_CONTENT {
            override fun asString(): String {
                return "agent.prompt.content"
            }
        },
        RESPONSE_CONTENT {
            override fun asString(): String {
                return "agent.response.content"
            }

        }
    }
}

class DefaultChatClientObservationConvention(private val name: String? = null) :
    ChatClientObservationConvention {
    private val defaultName = "agent.chat-client.call"

    private val promptContentNone: KeyValue = KeyValue.of(
        ChatClientObservationDocumentation.HighCardinalityKeyNames.PROMPT_CONTENT,
        KeyValue.NONE_VALUE
    )

    private val responseContentNone: KeyValue = KeyValue.of(
        ChatClientObservationDocumentation.HighCardinalityKeyNames.RESPONSE_CONTENT,
        KeyValue.NONE_VALUE
    )

    override fun getName(): String {
        return name ?: defaultName
    }

    override fun getLowCardinalityKeyValues(context: ChatClientRequestObservationContext): KeyValues {
        return KeyValues.empty()
    }

    override fun getHighCardinalityKeyValues(context: ChatClientRequestObservationContext): KeyValues {
        return KeyValues.of(promptContent(context), responseContent(context))
    }

    private fun promptContent(context: ChatClientRequestObservationContext): KeyValue {
        return context.carrier?.contents?.let { content ->
            KeyValue.of(
                ChatClientObservationDocumentation.HighCardinalityKeyNames.PROMPT_CONTENT,
                content
            )
        } ?: promptContentNone
    }

    private fun responseContent(context: ChatClientRequestObservationContext): KeyValue {
        return context.response?.let { response ->
            response.results.joinToString("\n") { it.output?.content ?: "" }
                .let { content ->
                    KeyValue.of(
                        ChatClientObservationDocumentation.HighCardinalityKeyNames.RESPONSE_CONTENT,
                        content
                    )
                }
        } ?: responseContentNone
    }
}

interface ChatClientObservationConvention :
    ObservationConvention<ChatClientRequestObservationContext> {
    override fun supportsContext(context: Observation.Context): Boolean {
        return context is ChatClientRequestObservationContext
    }
}


class ChatClientRequestObservationContext(val prompt: Prompt) :
    RequestReplySenderContext<Prompt, ChatResponse>({ _, _, _ ->
        run {}
    }) {
    init {
        setCarrier(prompt)
    }
}

class InstrumentedChatClient(
    val chatClient: ChatClient,
    private val observationRegistry: ObservationRegistry? = null,
) : ChatClient {
    override fun call(prompt: Prompt): ChatResponse {
        val action = { chatClient.call(prompt) }
        return observationRegistry?.let { registry ->
            instrumentedCall(prompt, action, registry)
        } ?: action.invoke()
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
                val response = action.invoke()
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
}