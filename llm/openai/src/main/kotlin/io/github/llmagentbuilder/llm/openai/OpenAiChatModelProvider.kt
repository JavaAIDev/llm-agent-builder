package io.github.llmagentbuilder.llm.openai

import io.github.llmagentbuilder.core.ChatModelProvider
import org.apache.commons.lang3.StringUtils
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.model.function.FunctionCallbackContext
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.retry.support.RetryTemplate

class OpenAiChatModelProvider : ChatModelProvider {
    override fun provideChatModel(
        functionCallbackContext: FunctionCallbackContext,
        config: Map<String, Any?>?,
    ): ChatModel {
        val apiKeyEnv = (config?.get("apiKey") as? String?) ?: "OPENAI_API_KEY"
        val apiKey = System.getenv(apiKeyEnv)
        if (StringUtils.isEmpty(apiKey)) {
            throw RuntimeException("OpenAI API key is required.")
        }
        val model = (config?.get("model") as? String?)
            ?: OpenAiApi.ChatModel.GPT_3_5_TURBO.value
        val chatModel = OpenAiChatModel(
            OpenAiApi(apiKey),
            OpenAiChatOptions.builder().withModel(model).build(),
            functionCallbackContext,
            RetryTemplate.defaultInstance()
        )
        return chatModel
    }
}