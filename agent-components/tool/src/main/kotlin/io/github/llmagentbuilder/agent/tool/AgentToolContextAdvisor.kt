package io.github.llmagentbuilder.agent.tool

import io.github.llmagentbuilder.core.tool.AgentTool
import org.springframework.ai.chat.client.AdvisedRequest
import org.springframework.ai.chat.client.advisor.api.RequestAdvisor

const val CONTEXT_KEY_TOOL_NAMES = "llmagentbuilder.toolNames"
const val CONTEXT_KEY_TOOLS = "llmagentbuilder.tools"
const val SYSTEM_PARAM_TOOL_NAMES = "tool_names"
const val SYSTEM_PARAM_TOOLS = "tools"

class AgentToolContextAdvisor(private val tools: Map<String, AgentTool<*, *>>) :
    RequestAdvisor {
    override fun getName(): String {
        return "AgentTool"
    }

    override fun adviseRequest(
        request: AdvisedRequest,
        adviseContext: MutableMap<String, Any>
    ): AdvisedRequest {
        val toolNames = tools.keys
        adviseContext[CONTEXT_KEY_TOOL_NAMES] = toolNames
        val toolsDescription = renderTools(tools.values)
        adviseContext[CONTEXT_KEY_TOOLS] = toolsDescription
        val systemParams = HashMap(request.systemParams ?: mapOf())
        systemParams[SYSTEM_PARAM_TOOLS] = toolsDescription
        systemParams[SYSTEM_PARAM_TOOL_NAMES] = toolNames.joinToString(", ")
        val functionNames =
            (HashSet(request.functionNames ?: listOf()) + tools.keys).toList()
        return AdvisedRequest.from(request)
            .withFunctionNames(functionNames)
            .withSystemParams(systemParams)
            .build()
    }

    private fun renderTools(tools: Collection<AgentTool<*, *>>): String {
        return tools.joinToString("\n") {
            "${it.name()}: ${it.description()}"
        }
    }
}