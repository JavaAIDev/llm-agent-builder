package io.github.llmagentbuilder.spring.dev

import io.github.llmagentbuilder.core.Agent
import io.github.llmagentbuilder.core.AgentInfo
import io.github.llmagentbuilder.core.AgentInfoBuilder
import io.github.llmagentbuilder.core.tool.AgentToolsProvider
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
        return AgentInfoBuilder.info(agent, agentToolsProvider)
    }
}