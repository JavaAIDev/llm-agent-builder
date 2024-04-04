package io.github.alexcheng1982.agentappbuilder.spring.dev

import io.github.alexcheng1982.agentappbuilder.core.Agent
import io.github.alexcheng1982.agentappbuilder.core.tool.AgentToolsProvider
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/_agent")
class AgentDevController(
    private val agent: Agent<*, *>,
    private val agentToolsProvider: AgentToolsProvider
) {

    @GetMapping("/info", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun info(): AgentInfo {
        return AgentInfo(
            agent.name(),
            agent.description(),
            agent.usageInstruction(),
            agentToolsProvider.get().values.map { tool ->
                AgentToolInfo(
                    tool.name(),
                    tool.description(),
                )
            }
        )
    }
}