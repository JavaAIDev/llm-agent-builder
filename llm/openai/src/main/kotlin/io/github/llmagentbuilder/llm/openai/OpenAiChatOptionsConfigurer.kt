package io.github.llmagentbuilder.llm.openai

import io.github.llmagentbuilder.core.ChatOptionsConfigurer
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.ai.openai.OpenAiChatOptions

class OpenAiChatOptionsConfigurer : ChatOptionsConfigurer {
    override fun supports(chatOptions: ChatOptions?): Boolean {
        return chatOptions is OpenAiChatOptions
    }

    override fun configure(
        chatOptions: ChatOptions?,
        config: ChatOptionsConfigurer.ChatOptionsConfig
    ): ChatOptions {
        val stops = config.stopSequence ?: listOf()
        return chatOptions?.let {
            OpenAiChatOptions.Builder(it as OpenAiChatOptions)
                .withStop(
                    (it.stopSequences ?: listOf()) + stops
                ).build()
        } ?: OpenAiChatOptions.builder().withStop(stops).build()
    }
}