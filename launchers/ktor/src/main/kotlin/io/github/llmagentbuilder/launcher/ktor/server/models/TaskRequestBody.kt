package io.github.llmagentbuilder.launcher.ktor.server.models


/**
 * Body of the task request.
 * @param input Input prompt for the task.
 * @param additionalInput Input parameters for the task. Any value is allowed.
 */
data class TaskRequestBody(
    /* Input prompt for the task. */
    val input: kotlin.String? = null,
    /* Input parameters for the task. Any value is allowed. */
    val additionalInput: kotlin.Any? = null
) 

