package com.javaaidev.llmagentbuilder.core

import io.micrometer.observation.ObservationRegistry

interface ObservationPlugin {
    fun install(
        agentConfig: com.javaaidev.llmagentbuilder.core.AgentConfig,
        observationRegistry: ObservationRegistry,
    )
}