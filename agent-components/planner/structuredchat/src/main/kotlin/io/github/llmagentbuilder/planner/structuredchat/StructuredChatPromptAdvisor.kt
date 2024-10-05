package io.github.llmagentbuilder.planner.structuredchat

import org.springframework.ai.chat.client.advisor.api.AdvisedRequest
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain
import org.springframework.core.Ordered

internal const val defaultSystemTextTemplate = """
{system_instruction}

You have access to the following tools:

{tools}

Use a json blob to specify a tool by providing an action key (tool name) and an action_input key (tool input).

Valid "action" values: "Final Answer" or {tool_names}

Provide only ONE action per ${'$'}JSON_BLOB, as shown:

```
{{
  "action": ${'$'}TOOL_NAME,
  "action_input": ${'$'}INPUT
}}
```

Follow this format:

Question: input question to answer
Thought: consider previous and subsequent steps
Action:
```
${'$'}JSON_BLOB
```
Observation: action result
... (repeat Thought/Action/Observation N times)
Thought: I know what to respond
Action:
```
{{
  "action": "Final Answer",
  "action_input": "Final response to human"
}}

Begin! Reminder to ALWAYS respond with a valid json blob of a single action. Use tools if necessary. Respond directly if appropriate. Format is Action:```${'$'}JSON_BLOB```then Observation    
"""

internal const val defaultUserTextTemplate = """
{user_input}

(reminder to respond in a JSON blob no matter what)
"""

class StructuredChatPromptAdvisor : CallAroundAdvisor {
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
        val request = AdvisedRequest.from(advisedRequest)
            .withSystemText(defaultSystemTextTemplate)
            .withSystemParams(systemParams)
            .withUserText(defaultUserTextTemplate)
            .withUserParams(userParams)
            .build()
        return chain.nextAroundCall(request)
    }
}