package io.github.llmagentbuilder.planner.react

import io.github.llmagentbuilder.planner.executor.LLMPlanExecutor
import org.springframework.ai.chat.client.ChatClient

class ReActPlanner(chatClient: ChatClient) :
    LLMPlanExecutor(chatClient, ReActOutputParser.INSTANCE)
