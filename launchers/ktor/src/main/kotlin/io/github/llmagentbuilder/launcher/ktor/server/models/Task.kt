package io.github.llmagentbuilder.launcher.ktor.server.models

/**
 *
 * @param taskId The ID of the task.
 * @param artifacts A list of artifacts that the task has produced.
 * @param input Input prompt for the task.
 * @param additionalInput Input parameters for the task. Any value is allowed.
 */
data class Task(
    /* The ID of the task. */
    val taskId: kotlin.String,
    /* A list of artifacts that the task has produced. */
    val artifacts: kotlin.collections.List<Artifact>,
    /* Input prompt for the task. */
    val input: kotlin.String? = null,
    /* Input parameters for the task. Any value is allowed. */
    val additionalInput: kotlin.Any? = null
) 

