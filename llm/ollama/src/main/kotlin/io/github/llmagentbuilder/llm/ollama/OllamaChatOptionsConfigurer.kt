package io.github.llmagentbuilder.llm.ollama

import io.github.llmagentbuilder.core.ChatOptionsConfigurer
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.ai.ollama.api.OllamaOptions

class OllamaChatOptionsConfigurer : ChatOptionsConfigurer {
    override fun supports(chatOptions: ChatOptions?): Boolean {
        return chatOptions is OllamaOptions
    }

    override fun configure(
        chatOptions: ChatOptions?,
        config: ChatOptionsConfigurer.ChatOptionsConfig
    ): ChatOptions {
        val stops = config.stopSequence ?: listOf()
        return chatOptions?.let {
            val options = OllamaOptions.fromOptions(it as OllamaOptions)
            options.stop = ((options.stopSequences ?: listOf()) + stops)
            return options
        } ?: OllamaOptions.builder().stop(stops).build()
    }
}