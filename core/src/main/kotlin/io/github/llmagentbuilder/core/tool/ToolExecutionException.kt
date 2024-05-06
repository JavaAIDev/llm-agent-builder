package io.github.llmagentbuilder.core.tool

class ToolExecutionException(
    private val tool: AgentTool<*, *>,
    cause: Throwable?
) : RuntimeException(cause) {
    override val message: String
        get() = "Execution error in tool ${tool.name()}"
}