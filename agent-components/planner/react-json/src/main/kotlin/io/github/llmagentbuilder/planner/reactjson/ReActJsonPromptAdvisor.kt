package io.github.llmagentbuilder.planner.reactjson

import io.github.llmagentbuilder.core.ChatOptionsConfigurer
import io.github.llmagentbuilder.planner.executor.ChatOptionsHelper
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain
import org.springframework.core.Ordered

internal const val defaultSystemTextTemplate = """
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

internal const val defaultUserTextTemplate = """
{user_input}

{agent_scratchpad}
"""

class ReActJsonPromptAdvisor : CallAroundAdvisor {
    override fun getName(): String {
        return javaClass.simpleName
    }

    override fun getOrder(): Int {
        return Ordered.HIGHEST_PRECEDENCE + 1000
    }

    override fun aroundCall(
        advisedRequest: AdvisedRequest,
        chain: CallAroundAdvisorChain
    ): AdvisedResponse {
        val systemParams = HashMap(advisedRequest.systemParams ?: mapOf())
        systemParams["system_instruction"] = advisedRequest.systemText ?: ""
        val userParams = HashMap(advisedRequest.userParams ?: mapOf())
        userParams["user_input"] = advisedRequest.userText ?: ""
        val chatOptions = ChatOptionsHelper.buildChatOptions(
            advisedRequest.chatOptions,
            ChatOptionsConfigurer.ChatOptionsConfig(listOf("Observation:"))
        )
        val request = AdvisedRequest.from(advisedRequest)
            .withSystemText(defaultSystemTextTemplate)
            .withSystemParams(systemParams)
            .withUserText(defaultUserTextTemplate)
            .withUserParams(userParams)
            .withChatOptions(chatOptions)
            .build()
        return chain.nextAroundCall(request)
    }
}