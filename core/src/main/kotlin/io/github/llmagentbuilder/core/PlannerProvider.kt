package io.github.llmagentbuilder.core

import io.micrometer.observation.ObservationRegistry
import org.springframework.ai.chat.client.ChatClient

interface PlannerProvider {
    fun configKey(): String

    fun providePlanner(
        chatClientBuilder: ChatClient.Builder,
        config: Map<String, Any?>? = null,
        observationRegistry: ObservationRegistry? = null,
    ): Planner?
}