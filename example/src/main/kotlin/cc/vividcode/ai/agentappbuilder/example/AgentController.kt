package cc.vividcode.ai.agentappbuilder.example

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AgentController(private val agentService: AgentService) {
    @PostMapping(
        "/chat",
        consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE),
        produces = arrayOf(MediaType.APPLICATION_JSON_VALUE)
    )
    fun chat(@RequestBody request: MathAgentRequest): MathAgentResponse {
        return agentService.call(request)
    }
}