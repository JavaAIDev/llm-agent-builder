package io.github.alexcheng1982.agentappbuilder.example

import io.github.alexcheng1982.agentappbuilder.core.ChatAgent
import io.github.alexcheng1982.agentappbuilder.core.ChatAgentRequest
import io.github.alexcheng1982.agentappbuilder.core.ChatAgentResponse

class AgentService(private val agent: ChatAgent) {
    fun call(request: ChatAgentRequest): ChatAgentResponse {
        return agent.call(request)
    }
}