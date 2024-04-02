package io.github.alexcheng1982.agentappbuilder.example

import io.github.alexcheng1982.agentappbuilder.core.Agent
import io.github.alexcheng1982.agentappbuilder.core.ChatAgentRequest
import io.github.alexcheng1982.agentappbuilder.core.ChatAgentResponse

class AgentService(private val agent: Agent<ChatAgentRequest, ChatAgentResponse>) {
    fun call(request: ChatAgentRequest): ChatAgentResponse {
        return agent.call(request)
    }
}