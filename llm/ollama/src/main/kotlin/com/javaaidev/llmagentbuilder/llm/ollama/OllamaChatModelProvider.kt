package com.javaaidev.llmagentbuilder.llm.ollama

import com.javaaidev.llmagentbuilder.core.ChatModelProvider
import com.javaaidev.llmagentbuilder.core.MapToObject
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.model.function.FunctionCallbackResolver
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.ollama.api.OllamaApi
import org.springframework.ai.ollama.api.OllamaModel
import org.springframework.ai.ollama.api.OllamaOptions

class OllamaChatModelProvider : ChatModelProvider {
    override fun configKey(): String {
        return "ollama"
    }

    override fun provideChatModel(
        functionCallbackResolver: FunctionCallbackResolver,
        config: Map<String, Any?>?
    ): ChatModel? {
        val ollamaConfig = MapToObject.toObject<OllamaConfig>(config)
        if (ollamaConfig?.enabled == false) {
            return null
        }
        val model = ollamaConfig?.model ?: OllamaModel.PHI3.id()
        return OllamaChatModel.builder()
            .ollamaApi(OllamaApi())
            .defaultOptions(OllamaOptions.builder().model(model).build())
            .functionCallbackResolver(functionCallbackResolver)
            .build()
    }
}