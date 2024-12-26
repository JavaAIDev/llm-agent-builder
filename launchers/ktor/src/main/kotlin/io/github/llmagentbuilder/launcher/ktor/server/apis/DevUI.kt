package io.github.llmagentbuilder.launcher.ktor.server.apis

import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.devUI() {
    get("/") {
        call.respondRedirect("/webjars/chat-agent-ui/index.html")
    }
    get("/_next/{path...}") {
        val redirectPath = call.parameters.getAll("path")?.joinToString("/")
        call.respondRedirect("/webjars/chat-agent-ui/${redirectPath}")
    }
}
