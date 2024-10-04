package io.github.llmagentbuilder.launcher.ktor.server.models

/**
 *
 * @param artifacts
 * @param pagination
 */
data class TaskArtifactsListResponse(
    val artifacts: kotlin.collections.List<Artifact>,
    val pagination: Pagination
) 

