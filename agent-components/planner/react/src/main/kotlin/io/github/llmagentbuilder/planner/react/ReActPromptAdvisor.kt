package io.github.llmagentbuilder.planner.react

import io.github.llmagentbuilder.core.ChatOptionsConfigurer
import io.github.llmagentbuilder.planner.executor.ChatOptionsHelper
import org.springframework.ai.chat.client.AdvisedRequest
import org.springframework.ai.chat.client.advisor.api.RequestAdvisor

const val defaultSystemTextTemplate = """
{system_instruction}

You have access to the following tools:

{tools}

Use the following format:

Question: the input question you must answer
Thought: you should always think about what to do
Action: the action to take, should be one of [{tool_names}]
Action Input: the input to the action
Observation: the result of the action
... (this Thought/Action/Action Input/Observation can repeat N times)
Thought: I now know the final answer
Final Answer: the final answer to the original input question

Begin!
    
"""

class ReActPromptAdvisor : RequestAdvisor {
    override fun getName(): String {
        return "ReAct Planner - Prompt"
    }

    override fun adviseRequest(
        request: AdvisedRequest,
        adviseContext: MutableMap<String, Any>
    ): AdvisedRequest {
        val systemParams = HashMap(request.systemParams ?: mapOf())
        systemParams["system_instruction"] = request.systemText ?: ""
        val chatOptions = ChatOptionsHelper.buildChatOptions(
            request.chatOptions,
            ChatOptionsConfigurer.ChatOptionsConfig(listOf("\\nObservation"))
        )
        return AdvisedRequest.from(request)
            .withSystemText(defaultSystemTextTemplate)
            .withSystemParams(systemParams)
            .withChatOptions(chatOptions)
            .build()
    }
}