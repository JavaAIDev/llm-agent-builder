package com.javaaidev.llmagentbuilder.launcher.ktor.server.apis

import com.javaaidev.llmagentbuilder.core.ChatAgent
import com.javaaidev.llmagentbuilder.core.ChatAgentRequest
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.agentApi(chatAgent: ChatAgent) {
    post("/chat") {
        val request = call.receive(ChatAgentRequest::class)
        call.respond(chatAgent.call(request))
    }
}
