package cc.vividcode.ai.agentappbuilder.core.react

import cc.vividcode.ai.agentappbuilder.core.AgentAction
import cc.vividcode.ai.agentappbuilder.core.planner.react.ReActOutputParser
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ReActOutputParserTest {

    @Test
    fun parse() {
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
}