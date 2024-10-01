package io.github.llmagentbuilder.planner.reactjson

import io.github.llmagentbuilder.core.Planner
import io.github.llmagentbuilder.core.PlannerProvider
import org.springframework.ai.chat.client.ChatClient

class ReActJsonPlannerProvider : PlannerProvider {
    override fun providePlanner(chatClientBuilder: ChatClient.Builder): Planner {
        val chatClient =
            chatClientBuilder.defaultAdvisors(ReActJsonPromptAdvisor())
                .build()
        return ReActJsonPlanner(chatClient)
    }
}