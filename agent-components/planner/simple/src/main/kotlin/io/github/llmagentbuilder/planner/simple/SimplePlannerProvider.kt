package io.github.llmagentbuilder.planner.simple

import io.github.llmagentbuilder.core.Planner
import io.github.llmagentbuilder.core.PlannerProvider
import org.springframework.ai.chat.client.ChatClient

class SimplePlannerProvider : PlannerProvider {
    override fun providePlanner(chatClientBuilder: ChatClient.Builder): Planner {
        return SimplePlanner(chatClientBuilder.build())
    }
}