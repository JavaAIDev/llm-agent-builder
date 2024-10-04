package io.github.llmagentbuilder.launcher.ktor.server.models


/**
 * An Artifact either created by or submitted to the agent.
 * @param artifactId ID of the artifact.
 * @param agentCreated Whether the artifact has been created by the agent.
 * @param fileName Filename of the artifact.
 * @param relativePath Relative path of the artifact in the agent's workspace.
 */
data class Artifact(
    /* ID of the artifact. */
    val artifactId: kotlin.String,
    /* Whether the artifact has been created by the agent. */
    val agentCreated: kotlin.Boolean,
    /* Filename of the artifact. */
    val fileName: kotlin.String,
    /* Relative path of the artifact in the agent's workspace. */
    val relativePath: kotlin.String? = null
) 

