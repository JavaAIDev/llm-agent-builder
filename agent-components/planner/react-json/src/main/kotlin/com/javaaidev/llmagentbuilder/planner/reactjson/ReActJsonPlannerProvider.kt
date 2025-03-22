package com.javaaidev.llmagentbuilder.planner.reactjson

import com.javaaidev.llmagentbuilder.core.MapToObject
import com.javaaidev.llmagentbuilder.core.Planner
import com.javaaidev.llmagentbuilder.core.PlannerProvider
import io.micrometer.observation.ObservationRegistry
import org.springframework.ai.chat.client.ChatClient

class ReActJsonPlannerProvider : PlannerProvider {
    override fun configKey(): String {
        return "reActJson"
    }

    override fun providePlanner(
        chatClientBuilder: ChatClient.Builder,
        config: Map<String, Any?>?,
        observationRegistry: ObservationRegistry?,
    ): Planner? {
        val plannerConfig = MapToObject.toObject<ReActJsonPlannerConfig>(config)
        if (plannerConfig?.enabled == false) {
            return null
        }
        val chatClient =
            chatClientBuilder.defaultAdvisors(ReActJsonPromptAdvisor())
                .build()
        return ReActJsonPlanner(chatClient, observationRegistry)
    }
}