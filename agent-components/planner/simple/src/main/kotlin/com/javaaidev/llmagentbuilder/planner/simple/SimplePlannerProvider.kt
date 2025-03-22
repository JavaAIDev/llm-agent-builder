package com.javaaidev.llmagentbuilder.planner.simple

import com.javaaidev.llmagentbuilder.core.MapToObject
import com.javaaidev.llmagentbuilder.core.Planner
import com.javaaidev.llmagentbuilder.core.PlannerProvider
import io.micrometer.observation.ObservationRegistry
import org.springframework.ai.chat.client.ChatClient

class SimplePlannerProvider : PlannerProvider {
    override fun configKey(): String {
        return "simple"
    }

    override fun providePlanner(
        chatClientBuilder: ChatClient.Builder,
        config: Map<String, Any?>?,
        observationRegistry: ObservationRegistry?,
    ): Planner? {
        val plannerConfig = MapToObject.toObject<SimplePlannerConfig>(config)
        if (plannerConfig?.enabled == false) {
            return null
        }
        return SimplePlanner(chatClientBuilder.build(), observationRegistry)
    }
}