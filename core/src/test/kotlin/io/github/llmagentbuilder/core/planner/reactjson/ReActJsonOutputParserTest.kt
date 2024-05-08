package io.github.llmagentbuilder.core.planner.reactjson

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
}