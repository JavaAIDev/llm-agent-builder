package com.javaaidev.llmagentbuilder.planner.simple

import com.javaaidev.llmagentbuilder.planner.executor.LLMPlanExecutor
import io.micrometer.observation.ObservationRegistry
import org.springframework.ai.chat.client.ChatClient

class SimplePlanner(
    chatClient: ChatClient,
    observationRegistry: ObservationRegistry? = null
) :
    LLMPlanExecutor(
        chatClient,
        SimpleOutputParser.INSTANCE,
        observationRegistry
    )