package io.github.alexcheng1982.agentappbuilder.example

import io.github.alexcheng1982.agentappbuilder.core.ChatAgentRequest
import io.github.alexcheng1982.agentappbuilder.core.ChatAgentResponse
import io.github.alexcheng1982.agentappbuilder.spring.chatagent.ChatAgentService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class AgentController(private val agentService: ChatAgentService) {
    @PostMapping(
        "/chat",
        consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE),
        produces = arrayOf(MediaType.APPLICATION_JSON_VALUE)
    )
    fun chat(@RequestBody request: ChatAgentRequest): ChatAgentResponse {
        return agentService.chat(request)
    }
}