package io.github.llmagentbuilder.planner.react

import io.github.llmagentbuilder.core.MapToObject
import io.github.llmagentbuilder.core.Planner
import io.github.llmagentbuilder.core.PlannerProvider
import org.springframework.ai.chat.client.ChatClient

class ReActPlannerProvider : PlannerProvider {
    override fun configKey(): String {
        return "reAct"
    }

    override fun providePlanner(
        chatClientBuilder: ChatClient.Builder,
        config: Map<String, Any?>?
    ): Planner? {
        val plannerConfig = MapToObject.toObject<ReActPlannerConfig>(config)
        if (plannerConfig?.enabled == false) {
            return null
        }
        val chatClient = chatClientBuilder.defaultAdvisors(ReActPromptAdvisor())
            .build()
        return ReActPlanner(chatClient)
    }
}