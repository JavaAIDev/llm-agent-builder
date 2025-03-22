package com.javaaidev.llmagentbuilder.planner.structuredchat

import com.javaaidev.llmagentbuilder.core.MapToObject
import com.javaaidev.llmagentbuilder.core.Planner
import com.javaaidev.llmagentbuilder.core.PlannerProvider
import io.micrometer.observation.ObservationRegistry
import org.springframework.ai.chat.client.ChatClient

class StructuredChatPlannerProvider : PlannerProvider {
    override fun configKey(): String {
        return "structuredChat"
    }

    override fun providePlanner(
        chatClientBuilder: ChatClient.Builder,
        config: Map<String, Any?>?,
        observationRegistry: ObservationRegistry?,
    ): Planner? {
        val plannerConfig =
            MapToObject.toObject<StructuredChatPlannerConfig>(config)
        if (plannerConfig?.enabled == false) {
            return null
        }
        val chatClient =
            chatClientBuilder.defaultAdvisors(StructuredChatPromptAdvisor())
                .build()
        return StructuredChatPlanner(chatClient, observationRegistry)
    }
}