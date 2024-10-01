package io.github.llmagentbuilder.planner.reactjson

import io.github.llmagentbuilder.planner.executor.LLMPlanExecutor
import org.springframework.ai.chat.client.ChatClient

class ReActJsonPlanner(chatClient: ChatClient) :
    LLMPlanExecutor(chatClient, ReActJsonOutputParser.INSTANCE)