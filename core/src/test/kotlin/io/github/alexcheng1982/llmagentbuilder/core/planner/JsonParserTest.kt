package io.github.alexcheng1982.llmagentbuilder.core.planner

import io.github.llmagentbuilder.core.planner.planner.JsonParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class JsonParserTest {

    @Test
    fun parseNoJsonName() {
        val text = """
            Thought: I need to find a list of 10 universities in Thailand. The appropriate tool for this task is 'searchUniversities'.
            Action:
            ```
            {
              "action": "searchUniversities",
              "action_input": {
                "country": "Thailand",
                "limit": 10
              }
            }
            ```
        """.trimIndent()
        val result = JsonParser.parse(text)
        assertNotNull(result)
        assertEquals("searchUniversities", result!!["action"])
    }

    @Test
    fun parseJsonMarkdown() {
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
        val result = JsonParser.parse(text)
        assertNotNull(result)
        assertEquals("searchUniversities", result!!["action"])
    }

    @Test
    fun parseAsNormalJson() {
        val text = """
            
{
    "action": "writeLocalFile",
    "action_input": {
        "url": "http://www.example.com",
        "filename": "static_map.png"
    }
}
        """.trimIndent()

        val result = JsonParser.parse(text)
        assertNotNull(result)
        assertEquals("writeLocalFile", result!!["action"])
    }
}