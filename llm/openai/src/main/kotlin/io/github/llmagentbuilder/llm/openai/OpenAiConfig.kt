package io.github.llmagentbuilder.llm.openai

data class OpenAiConfig(
    val enabled: Boolean? = true,
    val apiKey: String? = null,
    val apiKeyEnv: String? = null,
    val model: String? = null,
)