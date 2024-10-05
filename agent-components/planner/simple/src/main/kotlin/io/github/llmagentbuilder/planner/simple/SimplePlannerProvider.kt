package io.github.llmagentbuilder.planner.simple

import io.github.llmagentbuilder.core.MapToObject
import io.github.llmagentbuilder.core.Planner
import io.github.llmagentbuilder.core.PlannerProvider
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