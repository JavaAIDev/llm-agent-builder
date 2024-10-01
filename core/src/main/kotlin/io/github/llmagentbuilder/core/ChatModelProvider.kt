package io.github.llmagentbuilder.core

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.model.function.FunctionCallbackContext

interface ChatModelProvider {
    fun configKey(): String

    fun provideChatModel(
        functionCallbackContext: FunctionCallbackContext,
        config: Map<String, Any?>? = null,
    ): ChatModel?
}