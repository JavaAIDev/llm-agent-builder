package io.github.llmagentbuilder.launcher.ktor.server.apis

import io.github.llmagentbuilder.core.ChatAgent
import io.github.llmagentbuilder.core.ChatAgentRequest
import io.github.llmagentbuilder.launcher.ktor.server.models.Step
import io.github.llmagentbuilder.launcher.ktor.server.models.Task
import io.github.llmagentbuilder.launcher.ktor.server.models.TaskRequestBody
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

fun Route.agentApi(chatAgent: ChatAgent) {
    val tasks = ConcurrentHashMap<String, String>()

    post("/chat") {
        val request = call.receive(ChatAgentRequest::class)
        call.respond(chatAgent.call(request))
    }

    post("/ap/v1/agent/tasks") {
        val taskId = UUID.randomUUID().toString()
        val request = call.receive(TaskRequestBody::class)
        tasks[taskId] = request.input!!
        call.respond(Task(taskId, listOf()))
    }

//    get<Paths.downloadAgentTaskArtifact> {
//
//
//    }
//
    post("/ap/v1/agent/tasks/{task_id}/steps") {
        val taskId = call.parameters["task_id"]!!
        tasks[taskId]?.run {
            val response = chatAgent.call(ChatAgentRequest(this))
            call.respond(
                Step(
                    taskId,
                    UUID.randomUUID().toString(),
                    Step.Status.completed,
                    listOf(),
                    true,
                    output = response.output,
                )
            )
        } ?: run {
            call.respond(HttpStatusCode.NotFound, "task not found")
        }
    }
//
//    get<Paths.getAgentTask> {
//
//
//    }
//
//    get<Paths.getAgentTaskStep> {
//
//    }
//
//    get<Paths.listAgentTaskArtifacts> {
//
//    }
//
//    get<Paths.listAgentTaskSteps> {
//
//    }
//
//    get<Paths.listAgentTasks> {
//
//
//    }
//
//    post<Paths.uploadAgentTaskArtifacts> {
//
//    }

}
