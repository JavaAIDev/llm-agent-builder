package io.github.alexcheng1982.agentappbuilder.core

interface AgentToolFactory<out T : AgentTool<*, *>> {
    fun create(): T
}