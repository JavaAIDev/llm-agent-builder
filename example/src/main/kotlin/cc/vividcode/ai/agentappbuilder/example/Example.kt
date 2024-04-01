package cc.vividcode.ai.agentappbuilder.example

import cc.vividcode.ai.agentappbuilder.core.AgentRequest
import cc.vividcode.ai.agentappbuilder.core.AgentTool
import cc.vividcode.ai.agentappbuilder.core.AgentToolFactory

class AddTool : AgentTool<AddRequest, AddResponse> {
    override fun name(): String {
        return "add"
    }

    override fun description(): String {
        return "add two numbers"
    }

    override fun apply(t: AddRequest): AddResponse {
        return AddResponse(t.op1 + t.op2)
    }

}

class AddToolFactory : AgentToolFactory<AddTool> {
    override fun create(): AddTool {
        return AddTool()
    }
}

data class MathAgentRequest(private val input: String) : AgentRequest {
    override fun toMap(): Map<String, Any> {
        return mapOf("input" to input)
    }
}

data class MathAgentResponse(val result: String)