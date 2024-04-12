package io.github.alexcheng1982.agentbuilder.launcher.jdkhttpsync

import com.fasterxml.jackson.databind.ObjectMapper
import com.sun.net.httpserver.HttpServer
import io.github.alexcheng1982.agentappbuilder.core.config.AgentConfig
import io.github.alexcheng1982.agentappbuilder.core.config.ConfiguredAgentFactory
import io.github.alexcheng1982.agentappbuilder.core.tool.AutoDiscoveredAgentToolsProvider
import java.net.InetSocketAddress

class JdkHttpSyncLauncher {
    fun launch(agentConfig: AgentConfig) {
        val server = HttpServer.create(InetSocketAddress(8080), 10)
        val objectMapper = ObjectMapper()
        val chatAgent = ConfiguredAgentFactory.createChatAgent(agentConfig)
        val agentToolsProvider =
            agentConfig.toolsConfig.agentToolsProvider ?: AutoDiscoveredAgentToolsProvider
        server.createContext(
            "/api/_agent/info",
            AgentInfoHandler(objectMapper, chatAgent, agentToolsProvider)
        )
        server.createContext("/api/chat", ChatAgentHandler(objectMapper, chatAgent))
        server.start()
    }
}