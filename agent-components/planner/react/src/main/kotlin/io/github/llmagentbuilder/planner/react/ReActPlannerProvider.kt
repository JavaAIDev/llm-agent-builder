package io.github.llmagentbuilder.planner.react

import io.github.llmagentbuilder.core.Planner
import io.github.llmagentbuilder.core.PlannerProvider
import org.springframework.ai.chat.client.ChatClient

class ReActPlannerProvider : PlannerProvider {
    override fun providePlanner(chatClientBuilder: ChatClient.Builder): Planner {
        val chatClient = chatClientBuilder.defaultAdvisors(ReActPromptAdvisor())
            .build()
        return ReActPlanner(chatClient)
    }
}