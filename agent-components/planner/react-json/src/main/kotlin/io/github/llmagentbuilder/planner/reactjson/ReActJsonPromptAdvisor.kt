package io.github.llmagentbuilder.planner.reactjson

import io.github.llmagentbuilder.core.ChatOptionsConfigurer
import io.github.llmagentbuilder.planner.executor.ChatOptionsHelper
import org.springframework.ai.chat.client.AdvisedRequest
import org.springframework.ai.chat.client.advisor.api.RequestAdvisor

const val defaultSystemTextTemplate = """
{system_instruction}

You have access to the following tools:

{tools}

The way you use the tools is by specifying a json blob.
Specifically, this json should have a `action` key (with the name of the tool to use) and a `action_input` key (with the input to the tool going here).

The only values that should be in the "action" field are: {tool_names}

The ${'$'}JSON_BLOB should only contain a SINGLE action, do NOT return a list of multiple actions. Here is an example of a valid ${'$'}JSON_BLOB:

```
{{
  "action": ${'$'}TOOL_NAME,
  "action_input": ${'$'}INPUT
}}
```

ALWAYS use the following format:

Question: the input question you must answer
Thought: you should always think about what to do
Action:
```
${'$'}JSON_BLOB
```
Observation: the result of the action
... (this Thought/Action/Observation can repeat N times)
Thought: I now know the final answer
Final Answer: the final answer to the original input question

Begin! Reminder to always use the exact characters `Final Answer` when responding.

"""

const val defaultUserTextTemplate = """
{user_input}

{agent_scratchpad}

(reminder to respond in a JSON blob no matter what)
"""

class ReActJsonPromptAdvisor : RequestAdvisor {
    override fun getName(): String {
        return "ReAct Json Planner - Prompt"
    }

    override fun adviseRequest(
        request: AdvisedRequest,
        adviseContext: MutableMap<String, Any>
    ): AdvisedRequest {
        val systemParams = HashMap(request.systemParams ?: mapOf())
        systemParams["system_instruction"] = request.systemText ?: ""
        val userParams = HashMap(request.userParams ?: mapOf())
        userParams["user_input"] = request.userText ?: ""
        val chatOptions = ChatOptionsHelper.buildChatOptions(
            request.chatOptions,
            ChatOptionsConfigurer.ChatOptionsConfig(listOf("Observation:"))
        )
        return AdvisedRequest.from(request)
            .withSystemText(defaultSystemTextTemplate)
            .withSystemParams(systemParams)
            .withUserText(defaultUserTextTemplate)
            .withUserParams(userParams)
            .withChatOptions(chatOptions)
            .build()
    }
}