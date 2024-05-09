package io.github.llmagentbuilder.core.planner.reactjson

import io.github.llmagentbuilder.core.AgentAction
import org.junit.jupiter.api.Assertions.assertEquals
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

    @Test
    fun parseWithCodeExecution() {
        val text = """
            Thought: We need to read the 'users.csv' file, combine the 'first_name' and 'last_name' columns into a new 'name' column, and then write the updated data into a new file 'output.csv'. We can achieve this using the `executePythonCode` tool.

            Action:
            ```json
            {
              "action": "executePythonCode",
              "action_input": {
                "code": ""${'"'}
            import pandas as pd

            # Read the original CSV file
            df = pd.read_csv('users.csv')

            # Create a new column 'name' by combining first_name and last_name
            df['name'] = df['first_name'] + ' ' + df['last_name']

            # Drop the original columns and keep only 'name' and 'email'
            df = df[['name', 'email']]

            # Write the updated DataFrame to a new CSV file
            df.to_csv('output.csv', index=False)
                ""${'"'}
              }
            }
            ```
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
}