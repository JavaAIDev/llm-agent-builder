package io.github.llmagentbuilder.launcher.ktor.server

import io.github.llmagentbuilder.core.ChatAgent
import io.github.llmagentbuilder.launcher.ktor.server.apis.AgentApi
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*

object KtorLauncher {
    fun launch(chatAgent: ChatAgent) {
        embeddedServer(
            CIO,
            port = 8080,
            host = "0.0.0.0",
            module = {
                module(chatAgent)
            }
        ).start(wait = true)
    }
}

fun Application.module(chatAgent: ChatAgent) {
    install(DefaultHeaders)
    install(ContentNegotiation) {
        jackson()
    }
    install(
        Compression,
        ApplicationCompressionConfiguration()
    )
    install(Resources)
    install(Routing) {
        AgentApi(chatAgent)
    }
}
