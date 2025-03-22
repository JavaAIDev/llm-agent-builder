package com.javaaidev.llmagentbuilder.core

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.model.function.FunctionCallbackResolver

interface ChatModelProvider {
    fun configKey(): String

    fun provideChatModel(
        functionCallbackResolver: FunctionCallbackResolver,
        config: Map<String, Any?>? = null,
    ): ChatModel?
}