package io.github.llmagentbuilder.launcher.ktor.server

import io.github.llmagentbuilder.core.ChatAgent
import io.github.llmagentbuilder.core.FeatureConfig
import io.github.llmagentbuilder.core.LaunchConfig
import io.github.llmagentbuilder.launcher.ktor.server.apis.agentApi
import io.github.llmagentbuilder.launcher.ktor.server.apis.devUI
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import io.ktor.server.webjars.*
import org.slf4j.LoggerFactory

object KtorLauncher {
    private val logger = LoggerFactory.getLogger(KtorLauncher::class.java)

    fun launch(chatAgent: ChatAgent, launchConfig: LaunchConfig? = null) {
        val host = launchConfig?.server?.host ?: "localhost"
        val port = launchConfig?.server?.port ?: 8080
        logger.info("Starting agent server: $host:$port")
        embeddedServer(
            CIO,
            port,
            host,
            module = {
                module(chatAgent)
            }
        ).start(wait = true)
    }
}

fun Application.module(
    chatAgent: ChatAgent,
    featureConfig: FeatureConfig? = null
) {
    install(Webjars) {
        path = "/webjars"
    }
    install(DefaultHeaders)
    install(ContentNegotiation) {
        jackson()
    }
    install(
        Compression,
        applicationCompressionConfiguration()
    )
    install(Resources)
    routing {
        agentApi(chatAgent)
        if (featureConfig?.devUiEnabled != false) {
            devUI()
        }
    }
}
