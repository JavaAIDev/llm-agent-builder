package io.github.llmagentbuilder.core.planner

import org.springframework.ai.chat.prompt.ChatOptions

interface ChatOptionsConfigurer {

    data class ChatOptionsConfig(
        val functions: Set<String>? = null,
        val stopSequence: List<String>? = null,
    )

    fun supports(chatOptions: ChatOptions): Boolean
    fun configure(
        chatOptions: ChatOptions,
        config: ChatOptionsConfig
    ): ChatOptions
}