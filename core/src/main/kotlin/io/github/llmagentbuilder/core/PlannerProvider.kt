package io.github.llmagentbuilder.core

import org.springframework.ai.chat.client.ChatClient

interface PlannerProvider {
    fun configKey(): String

    fun providePlanner(
        chatClientBuilder: ChatClient.Builder,
        config: Map<String, Any?>? = null,
    ): Planner?
}