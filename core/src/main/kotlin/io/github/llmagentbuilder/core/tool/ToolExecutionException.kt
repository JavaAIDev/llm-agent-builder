package io.github.llmagentbuilder.core.tool

import com.javaaidev.easyllmtools.llmtoolspec.Tool

class ToolExecutionException(
    private val tool: Tool<*, *>,
    cause: Throwable?
) : RuntimeException(cause) {
    override val message: String
        get() = "Execution error in tool ${tool.name}"
}