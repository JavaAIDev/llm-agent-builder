package io.github.alexcheng1982.agentappbuilder.spring.dev

data class AgentToolInfo(
    val name: String,
    val description: String,
)

data class AgentInfo(
    val name: String,
    val description: String,
    val usageInstruction: String = "",
    val tools: List<AgentToolInfo> = listOf(),
)