package io.github.llmagentbuilder.core.observation

import io.micrometer.common.KeyValue
import io.micrometer.common.KeyValues
import io.micrometer.common.docs.KeyName
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationConvention
import io.micrometer.observation.docs.ObservationDocumentation
import io.micrometer.observation.transport.RequestReplySenderContext
import org.springframework.ai.chat.ChatResponse
import org.springframework.ai.chat.prompt.Prompt

enum class ChatClientObservationDocumentation : ObservationDocumentation {
    CHAT_CLIENT_CALL {
        override fun getDefaultConvention(): Class<out ObservationConvention<out Observation.Context>> {
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

        },
        PROMPT_TOKENS_COUNT {
            override fun asString(): String {
                return "agent.prompt.tokens.count"
            }
        },
        GENERATION_TOKENS_COUNT {
            override fun asString(): String {
                return "agent.generation.tokens.count"
            }
        },
        TOTAL_TOKENS_COUNT {
            override fun asString(): String {
                return "agent.total.tokens.count"
            }
        },
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

    private val promptTokensCountNone: KeyValue = KeyValue.of(
        ChatClientObservationDocumentation.HighCardinalityKeyNames.PROMPT_TOKENS_COUNT,
        KeyValue.NONE_VALUE
    )

    private val generationTokensCountNone: KeyValue = KeyValue.of(
        ChatClientObservationDocumentation.HighCardinalityKeyNames.GENERATION_TOKENS_COUNT,
        KeyValue.NONE_VALUE
    )

    private val totalTokensCountNone: KeyValue = KeyValue.of(
        ChatClientObservationDocumentation.HighCardinalityKeyNames.TOTAL_TOKENS_COUNT,
        KeyValue.NONE_VALUE
    )

    override fun getName(): String {
        return name ?: defaultName
    }

    override fun getLowCardinalityKeyValues(context: ChatClientRequestObservationContext): KeyValues {
        return KeyValues.empty()
    }

    override fun getHighCardinalityKeyValues(context: ChatClientRequestObservationContext): KeyValues {
        return KeyValues.of(
            promptContent(context),
            responseContent(context),
            promptTokensCount(context),
            generationTokensCount(context),
            totalTokensCount(context)
        )
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

    private fun promptTokensCount(context: ChatClientRequestObservationContext): KeyValue {
        return context.response?.metadata?.usage?.promptTokens?.let { tokens ->
            KeyValue.of(
                ChatClientObservationDocumentation.HighCardinalityKeyNames.PROMPT_TOKENS_COUNT,
                tokens.toString()
            )
        } ?: promptTokensCountNone
    }

    private fun generationTokensCount(context: ChatClientRequestObservationContext): KeyValue {
        return context.response?.metadata?.usage?.generationTokens?.let { tokens ->
            KeyValue.of(
                ChatClientObservationDocumentation.HighCardinalityKeyNames.GENERATION_TOKENS_COUNT,
                tokens.toString()
            )
        } ?: generationTokensCountNone
    }

    private fun totalTokensCount(context: ChatClientRequestObservationContext): KeyValue {
        return context.response?.metadata?.usage?.totalTokens?.let { tokens ->
            KeyValue.of(
                ChatClientObservationDocumentation.HighCardinalityKeyNames.TOTAL_TOKENS_COUNT,
                tokens.toString()
            )
        } ?: totalTokensCountNone
    }
}

interface ChatClientObservationConvention :
    ObservationConvention<ChatClientRequestObservationContext> {
    override fun supportsContext(context: Observation.Context): Boolean {
        return context is ChatClientRequestObservationContext
    }
}


class ChatClientRequestObservationContext(prompt: Prompt) :
    RequestReplySenderContext<Prompt, ChatResponse>({ _, _, _ ->
        run {}
    }) {
    init {
        setCarrier(prompt)
    }
}

