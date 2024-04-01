package cc.vividcode.ai.agentappbuilder.example

import cc.vividcode.ai.agentappbuilder.core.AgentRequest
import cc.vividcode.ai.agentappbuilder.core.AgentTool
import cc.vividcode.ai.agentappbuilder.core.AgentToolFactory

data class AddRequest(val op1: Int, val op2: Int)
data class AddResponse(val result: Int)

class AddTool : AgentTool<AddRequest, AddResponse> {
    override fun name(): String {
        return "add"
    }

    override fun description(): String {
        return "add two numbers"
    }

    override fun run(input: Map<String, Any>): AddResponse {
        return AddResponse(
            (input["op1"] as? Int ?: 0) + (input["op2"] as? Int ?: 0)
        )
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

data class MathAgentResponse(val result: Int)