package io.github.llmagentbuilder.planner.simple

import io.github.llmagentbuilder.planner.executor.LLMPlanExecutor
import org.springframework.ai.chat.client.ChatClient

class SimplePlanner(chatClient: ChatClient) :
    LLMPlanExecutor(chatClient, SimpleOutputParser.INSTANCE) {
}