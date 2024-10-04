package io.github.llmagentbuilder.launcher.ktor.server.models


/**
 * Body of the task request.
 * @param input Input prompt for the step.
 * @param additionalInput Input parameters for the task step. Any value is allowed.
 */
data class StepRequestBody(
    /* Input prompt for the step. */
    val input: kotlin.String? = null,
    /* Input parameters for the task step. Any value is allowed. */
    val additionalInput: kotlin.Any? = null
) 

