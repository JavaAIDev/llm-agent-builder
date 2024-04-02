package io.github.alexcheng1982.agentappbuilder.example

import io.github.alexcheng1982.agentappbuilder.core.Agent

class AgentService(private val agent: Agent<MathAgentRequest, MathAgentResponse>) {
    fun call(request: MathAgentRequest): MathAgentResponse {
        return agent.call(request)
    }
}