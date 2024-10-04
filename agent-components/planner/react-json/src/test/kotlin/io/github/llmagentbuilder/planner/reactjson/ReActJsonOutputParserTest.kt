package io.github.llmagentbuilder.planner.reactjson

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.llmagentbuilder.core.AgentAction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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

    @Test
    fun parseActionInput() {
        val text = """
            Thought: I need to start a game  with the user. The first step is to validate the idiom provided by the user and then generate a response idiom that starts with the last character of the user's input.
Action:
```json
{
  "action": "checkChineseIdiom",
  "action_input": "{\"word\": \"一马当先\"}"
}
```
        """.trimIndent()
        val parser = ReActJsonOutputParser()
        val result = parser.parse(text)
        assertNotNull(result.action)
        assertEquals(
            "{\"word\": \"一马当先\"}",
            (result.action as AgentAction).toolInput
        )
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("testCases")
    fun parseInput(text: String, caseName: String) {
        val parser = ReActJsonOutputParser()
        val result = parser.parse(text)
        assertNotNull(result.action)
        val input = (result.action as AgentAction).toolInput
        assertNotNull(input)
        ObjectMapper().readValue(
            input,
            object : TypeReference<Map<String, Any>>() {})
    }

    private fun testCases(): List<Any> {
        return listOf(
            "double-quotes-escape.txt",
            "triple-quotes-escape.txt",
            "code.txt",
            "python-code.txt",
        ).map { arrayOf(loadText(it), it) }
    }

    private fun loadText(name: String): String {
        return javaClass.getResourceAsStream("/output_parser/ReActJson/$name")
            ?.reader()?.readText() ?: throw RuntimeException("$name not found")
    }
}