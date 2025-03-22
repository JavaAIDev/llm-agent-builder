package io.github.llmagentbuilder.llm.openai

import io.github.llmagentbuilder.core.ChatModelProvider
import io.github.llmagentbuilder.core.MapToObject
import org.apache.commons.lang3.StringUtils
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.model.function.FunctionCallbackResolver
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.retry.support.RetryTemplate

class OpenAiChatModelProvider : ChatModelProvider {
    override fun configKey(): String {
        return "openai"
    }

    override fun provideChatModel(
        functionCallbackResolver: FunctionCallbackResolver,
        config: Map<String, Any?>?,
    ): ChatModel? {
        val openAiConfig = MapToObject.toObject<OpenAiConfig>(config)
        if (openAiConfig?.enabled == false) {
            return null
        }
        val apiKey = (openAiConfig?.apiKey) ?: System.getenv(
            (openAiConfig?.apiKeyEnv) ?: "OPENAI_API_KEY"
        )
        if (StringUtils.isEmpty(apiKey)) {
            throw RuntimeException("OpenAI API key is required.")
        }
        val model =
            openAiConfig?.model ?: OpenAiApi.ChatModel.GPT_4_O_MINI.value
        val chatModel = OpenAiChatModel(
            OpenAiApi(apiKey),
            OpenAiChatOptions.builder().model(model).build(),
            functionCallbackResolver,
            RetryTemplate.defaultInstance()
        )
        return chatModel
    }
}