package io.github.llmagentbuilder.launcher.ktor.server.models

/**
 *
 * @param steps
 * @param pagination
 */
data class TaskStepsListResponse(
    val steps: kotlin.collections.List<Step>,
    val pagination: Pagination
) 

