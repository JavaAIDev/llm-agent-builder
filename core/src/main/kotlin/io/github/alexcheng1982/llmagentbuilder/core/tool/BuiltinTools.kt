package io.github.alexcheng1982.llmagentbuilder.core.tool

data class InvalidToolInput(
    val requestedToolName: String,
    val availableToolNames: Set<String>,
)

class InvalidTool : AgentTool<InvalidToolInput, String> {
    override fun name(): String {
        return "invalidTool"
    }

    override fun description(): String {
        return "Called when tool name is invalid. Suggests valid tool names."
    }

    override fun apply(t: InvalidToolInput): String {
        return """
            ${t.requestedToolName} is not a valid tool, try one of [${
            t.availableToolNames.joinToString(
                ", "
            )
        }].
        """.trimIndent()
    }
}