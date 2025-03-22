package com.javaaidev.llmagentbuilder.launcher.ktor.server

import com.fasterxml.jackson.databind.DeserializationFeature
import com.javaaidev.llmagentbuilder.launcher.ktor.server.apis.agentApi
import com.javaaidev.llmagentbuilder.launcher.ktor.server.apis.devUI
import com.javaaidev.llmagentbuilder.core.ChatAgent
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

    fun launch(
        chatAgent: ChatAgent,
        launchConfig: com.javaaidev.llmagentbuilder.core.LaunchConfig? = null
    ) {
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
    featureConfig: com.javaaidev.llmagentbuilder.core.FeatureConfig? = null
) {
    install(Webjars) {
        path = "/webjars"
    }
    install(DefaultHeaders)
    install(ContentNegotiation) {
        jackson {
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
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
