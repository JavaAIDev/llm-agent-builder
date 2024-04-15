package io.github.alexcheng1982.agentbuilder.launcher.jdkhttpsync

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.sun.net.httpserver.HttpServer
import io.github.alexcheng1982.agentappbuilder.core.config.AgentConfig
import io.github.alexcheng1982.agentappbuilder.core.config.ConfiguredAgentFactory
import io.github.alexcheng1982.agentappbuilder.core.tool.AutoDiscoveredAgentToolsProvider
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.util.concurrent.Executors

data class ServerOptions(
    val host: String = "localhost",
    val port: Int = 8080,
    val backlog: Int = 0,
)

data class PathOptions(
    val chatAgentPath: String = "/api/chat",
    val agentInfoPath: String? = "/api/_agent/info",
)

data class LaunchOptions(
    val serverOptions: ServerOptions = ServerOptions(),
    val pathOptions: PathOptions = PathOptions(),
)

class JdkHttpSyncLauncher {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun launch(
        agentConfig: AgentConfig,
        launchOptions: LaunchOptions = LaunchOptions()
    ) {
        val (serverOptions) = launchOptions
        val (host, port, backlog) = serverOptions
        val server = HttpServer.create(
            InetSocketAddress(host, port),
            backlog
        )
        server.executor = Executors.newSingleThreadScheduledExecutor(
            Thread.ofVirtual().name("agent-", 1).factory()
        )
        val objectMapper =
            ObjectMapper().registerModule(KotlinModule.Builder().build())
        val chatAgent = ConfiguredAgentFactory.createChatAgent(agentConfig)
        val agentToolsProvider =
            agentConfig.toolsConfig().agentToolsProvider
                ?: AutoDiscoveredAgentToolsProvider

        val (chatAgentPath, agentInfoPath) = launchOptions.pathOptions
        chatAgentPath.let { path ->
            server.createContext(
                path,
                ChatAgentHandler(objectMapper, chatAgent)
            )
        }

        agentInfoPath?.let { path ->
            server.createContext(
                path,
                AgentInfoHandler(objectMapper, chatAgent, agentToolsProvider)
            )
        } ?: {
            logger.info("Skipped agent info")
        }

        logger.info("Started Agent server at {}:{}", host, port)
        server.start()
    }
}