package io.github.alexcheng1982.agentappbuilder.core.config

import io.github.alexcheng1982.agentappbuilder.core.AgentFactory
import io.github.alexcheng1982.agentappbuilder.core.ChatAgent

object ConfiguredAgentFactory {
    fun createChatAgent(config: AgentConfig): ChatAgent {
        val (name, description, id, usageInstruction) = config.metadataConfig()
        val (plannerType) = config.plannerConfig()
        val (agentToolsProvider) = config.toolsConfig()
        val (observationRegistry) = config.observationConfig()
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