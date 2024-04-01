package cc.vividcode.ai.agentappbuilder.example

import cc.vividcode.ai.agentappbuilder.core.Agent

class AgentService(private val agent: Agent<MathAgentRequest, MathAgentResponse>) {
    fun call(request: MathAgentRequest): MathAgentResponse {
        return agent.call(request)
    }
}