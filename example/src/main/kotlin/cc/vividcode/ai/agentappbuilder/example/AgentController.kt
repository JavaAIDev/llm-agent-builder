package cc.vividcode.ai.agentappbuilder.example

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AgentController(private val agentService: AgentService) {
    @PostMapping("/chat")
    fun chat(request: MathAgentRequest): MathAgentResponse {
        return agentService.call(request)
    }
}