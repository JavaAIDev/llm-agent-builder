package cc.vividcode.ai.agentappbuilder.core

interface AgentToolFactory<out T : AgentTool<*, *>> {
    fun create(): T
}