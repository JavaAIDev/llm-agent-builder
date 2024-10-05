package io.github.llmagentbuilder.core

import io.micrometer.observation.ObservationRegistry

interface ObservationPlugin {
    fun install(
        agentConfig: AgentConfig,
        observationRegistry: ObservationRegistry
    )
}