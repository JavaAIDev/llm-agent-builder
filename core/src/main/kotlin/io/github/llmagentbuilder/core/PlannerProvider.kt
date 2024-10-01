package io.github.llmagentbuilder.core

import org.springframework.ai.chat.client.ChatClient

interface PlannerProvider {
    fun providePlanner(chatClientBuilder: ChatClient.Builder): Planner
}