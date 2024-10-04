package io.github.llmagentbuilder.launcher.ktor.server.models

/**
 *
 * @param taskId The ID of the task this step belongs to.
 * @param stepId The ID of the task step.
 * @param status The status of the task step.
 * @param artifacts A list of artifacts that the step has produced.
 * @param isLast Whether this is the last step in the task.
 * @param input Input prompt for the step.
 * @param additionalInput Input parameters for the task step. Any value is allowed.
 * @param name The name of the task step.
 * @param output Output of the task step.
 * @param additionalOutput Output that the task step has produced. Any value is allowed.
 */
data class Step(
    /* The ID of the task this step belongs to. */
    val taskId: kotlin.String,
    /* The ID of the task step. */
    val stepId: kotlin.String,
    /* The status of the task step. */
    val status: Step.Status,
    /* A list of artifacts that the step has produced. */
    val artifacts: kotlin.collections.List<Artifact>,
    /* Whether this is the last step in the task. */
    val isLast: kotlin.Boolean,
    /* Input prompt for the step. */
    val input: kotlin.String? = null,
    /* Input parameters for the task step. Any value is allowed. */
    val additionalInput: kotlin.Any? = null,
    /* The name of the task step. */
    val name: kotlin.String? = null,
    /* Output of the task step. */
    val output: kotlin.String? = null,
    /* Output that the task step has produced. Any value is allowed. */
    val additionalOutput: kotlin.Any? = null
) {
    /**
     * The status of the task step.
     * Values: created,running,completed
     */
    enum class Status(val value: kotlin.String) {
        created("created"),
        running("running"),
        completed("completed");
    }
}

