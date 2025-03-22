package io.github.llmagentbuilder.core.tool

import com.javaaidev.easyllmtools.llmtoolspec.Tool
import java.lang.reflect.Type

data class InvalidToolInput(
    val requestedToolName: String,
    val availableToolNames: Set<String>,
)

class InvalidTool : Tool<InvalidToolInput, String> {
    override fun getName(): String {
        return "invalidTool"
    }

    override fun getDescription(): String {
        return "Called when tool name is invalid. Suggests valid tool names."
    }

    override fun getRequestType(): Type {
        return InvalidToolInput::class.java
    }

    override fun call(t: InvalidToolInput): String {
        return """
            ${t.requestedToolName} is not a valid tool, try one of [${
            t.availableToolNames.joinToString(
                ", "
            )
        }].
        """.trimIndent()
    }
}