package io.github.llmagentbuilder.launcher.ktor.server.models

/**
 *
 * @param tasks
 * @param pagination
 */
data class TaskListResponse(
    val tasks: kotlin.collections.List<Task>,
    val pagination: Pagination
) 

