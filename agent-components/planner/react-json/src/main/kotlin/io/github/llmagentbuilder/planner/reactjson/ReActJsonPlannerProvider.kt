package io.github.llmagentbuilder.planner.reactjson

import io.github.llmagentbuilder.core.MapToObject
import io.github.llmagentbuilder.core.Planner
import io.github.llmagentbuilder.core.PlannerProvider
import org.springframework.ai.chat.client.ChatClient

class ReActJsonPlannerProvider : PlannerProvider {
    override fun configKey(): String {
        return "reActJson"
    }

    override fun providePlanner(
        chatClientBuilder: ChatClient.Builder,
        config: Map<String, Any?>?
    ): Planner? {
        val plannerConfig = MapToObject.toObject<ReActJsonPlannerConfig>(config)
        if (plannerConfig?.enabled == false) {
            return null
        }
        val chatClient =
            chatClientBuilder.defaultAdvisors(ReActJsonPromptAdvisor())
                .build()
        return ReActJsonPlanner(chatClient)
    }
}