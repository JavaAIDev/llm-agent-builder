package io.github.llmagentbuilder.planner.structuredchat

import io.github.llmagentbuilder.planner.executor.LLMPlanExecutor
import org.springframework.ai.chat.client.ChatClient

class StructuredChatPlanner(chatClient: ChatClient) :
    LLMPlanExecutor(chatClient, StructuredChatOutputParser.INSTANCE)