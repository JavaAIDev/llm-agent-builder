package io.github.alexcheng1982.agentappbuilder.core.config

import io.github.alexcheng1982.agentappbuilder.core.AgentFactory
import io.github.alexcheng1982.agentappbuilder.core.ChatAgent

object ConfiguredAgentFactory {
    fun createChatAgent(config: AgentConfig): ChatAgent {
        val (name, description, id, usageInstruction) = config.metadataConfig
            ?: MetadataConfig()
        val (plannerType) = config.plannerConfig ?: PlannerConfig()
        val (agentToolsProvider) = config.toolsConfig ?: ToolsConfig()
        val (observationRegistry) = config.observationConfig
            ?: ObservationConfig()
        return AgentFactory.createChatAgent(
            plannerType.create(config),
            name,
            description,
            usageInstruction,
            agentToolsProvider,
            id,
            observationRegistry,
        )
    }
}