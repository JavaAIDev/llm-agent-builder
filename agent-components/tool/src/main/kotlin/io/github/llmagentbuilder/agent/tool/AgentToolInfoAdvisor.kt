package io.github.llmagentbuilder.agent.tool

import com.javaaidev.easyllmtools.llmtoolspec.Tool
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain
import org.springframework.core.Ordered

const val SYSTEM_PARAM_TOOL_NAMES = "tool_names"
const val SYSTEM_PARAM_TOOLS = "tools"

class AgentToolInfoAdvisor(private val tools: Map<String, Tool<*, *>>) :
    CallAroundAdvisor {
    override fun getName(): String {
        return javaClass.simpleName
    }

    override fun getOrder(): Int {
        return Ordered.HIGHEST_PRECEDENCE + 500
    }

    override fun aroundCall(
        advisedRequest: AdvisedRequest,
        chain: CallAroundAdvisorChain
    ): AdvisedResponse {
        val toolNames = tools.keys
        val toolsDescription = renderTools(tools.values)
        val systemParams = HashMap(advisedRequest.systemParams)
        systemParams[SYSTEM_PARAM_TOOLS] = toolsDescription
        systemParams[SYSTEM_PARAM_TOOL_NAMES] = toolNames.joinToString(", ")
        val functionNames =
            (HashSet(advisedRequest.functionNames) + tools.keys).toList()
        val request = AdvisedRequest.from(advisedRequest)
            .functionNames(functionNames)
            .systemParams(systemParams)
            .build()
        return chain.nextAroundCall(request)
    }

    private fun renderTools(tools: Collection<Tool<*, *>>): String {
        return tools.joinToString("\n") {
            "${it.name}: ${it.description}"
        }
    }
}