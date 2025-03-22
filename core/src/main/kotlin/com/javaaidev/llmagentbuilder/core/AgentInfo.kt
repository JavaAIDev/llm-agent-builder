package com.javaaidev.llmagentbuilder.core

import com.javaaidev.llmagentbuilder.core.tool.AgentToolsProvider

data class AgentToolInfo(
    val name: String,
    val description: String,
)

data class AgentInfo(
    val id: String,
    val name: String,
    val description: String,
    val usageInstruction: String = "",
    val tools: List<AgentToolInfo> = listOf(),
)

object AgentInfoBuilder {
    fun info(
        agent: Agent<*, *>,
        agentToolsProvider: AgentToolsProvider
    ): AgentInfo {
        return AgentInfo(
            agent.id(),
            agent.name(),
            agent.description(),
            agent.usageInstruction(),
            agentToolsProvider.get().values.map { tool ->
                AgentToolInfo(
                    tool.name,
                    tool.description,
                )
            }
        )
    }
}