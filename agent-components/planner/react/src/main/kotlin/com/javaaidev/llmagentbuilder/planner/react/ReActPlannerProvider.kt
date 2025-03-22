package com.javaaidev.llmagentbuilder.planner.react

import com.javaaidev.llmagentbuilder.core.MapToObject
import com.javaaidev.llmagentbuilder.core.Planner
import com.javaaidev.llmagentbuilder.core.PlannerProvider
import io.micrometer.observation.ObservationRegistry
import org.springframework.ai.chat.client.ChatClient

class ReActPlannerProvider : PlannerProvider {
    override fun configKey(): String {
        return "reAct"
    }

    override fun providePlanner(
        chatClientBuilder: ChatClient.Builder,
        config: Map<String, Any?>?,
        observationRegistry: ObservationRegistry?,
    ): Planner? {
        val plannerConfig = MapToObject.toObject<ReActPlannerConfig>(config)
        if (plannerConfig?.enabled == false) {
            return null
        }
        val chatClient = chatClientBuilder.defaultAdvisors(ReActPromptAdvisor())
            .build()
        return ReActPlanner(chatClient, observationRegistry)
    }
}