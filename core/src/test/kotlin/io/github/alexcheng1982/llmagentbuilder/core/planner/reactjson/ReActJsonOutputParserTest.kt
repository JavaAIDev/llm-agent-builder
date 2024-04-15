package io.github.alexcheng1982.llmagentbuilder.core.planner.reactjson

import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class ReActJsonOutputParserTest {

    @Test
    fun parse() {
        val text = """
            Thought: I need to find a list of 10 universities in Thailand. The appropriate tool for this task is 'searchUniversities'.
            Action:
            ```json
            {
              "action": "searchUniversities",
              "action_input": {
                "country": "Thailand",
                "limit": 10
              }
            }
            ```
        """.trimIndent()
        val parser = ReActJsonOutputParser()
        val result = parser.parse(text)
        assertNotNull(result.action)
    }

    @Test
    fun parseWithCode() {
        val text = """
            
{
    "action": "writeLocalFile",
    "action_input": {
        "url": "http://www.example.com",
        "filename": "static_map.png"
    }
}
        """.trimIndent()
        val parser = ReActJsonOutputParser()
        val result = parser.parse(text)
        assertNotNull(result.action)
    }
}