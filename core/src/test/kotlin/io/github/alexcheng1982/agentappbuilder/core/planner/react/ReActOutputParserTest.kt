package io.github.alexcheng1982.agentappbuilder.core.planner.react

import io.github.alexcheng1982.llmagentbuilder.core.AgentAction
import io.github.alexcheng1982.llmagentbuilder.core.AgentFinish
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ReActOutputParserTest {

    @Test
    fun parseAction() {
        val text = """
            Thought: The user is asking for the sum of two numbers, which can be computed using the 'add' tool.
            Action: add
            Action Input: {"op1": 100, "op2": 200}
        """.trimIndent()
        val parser = ReActOutputParser()
        val result = parser.parse(text)
        assertNotNull(result.action)
        assertEquals("add", (result.action as AgentAction).tool)
    }

    @Test
    fun parseFinalAnswer() {
        val text = """
            Thought: I have the final answer.
            Final Answer: The result of adding 1 to 2 is 3.
        """.trimIndent()
        val parser = ReActOutputParser()
        val result = parser.parse(text)
        assertNotNull(result.finish)
        assertEquals(
            mapOf(
                "output" to "The result of adding 1 to 2 is 3."
            ), (result.finish as AgentFinish).returnValues
        )
    }
}