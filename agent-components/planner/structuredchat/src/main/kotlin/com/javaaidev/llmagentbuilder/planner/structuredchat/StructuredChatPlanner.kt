package com.javaaidev.llmagentbuilder.planner.structuredchat

import com.javaaidev.llmagentbuilder.planner.executor.LLMPlanExecutor
import io.micrometer.observation.ObservationRegistry
import org.springframework.ai.chat.client.ChatClient

class StructuredChatPlanner(
    chatClient: ChatClient,
    observationRegistry: ObservationRegistry? = null,
) :
    LLMPlanExecutor(
        chatClient,
        StructuredChatOutputParser.INSTANCE,
        observationRegistry
    )