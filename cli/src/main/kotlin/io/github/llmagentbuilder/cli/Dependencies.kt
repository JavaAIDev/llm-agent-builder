package io.github.llmagentbuilder.cli

data class MavenCoordinate(
    val groupId: String = "io.github.llmagentbuilder",
    val artifactId: String,
    val version: String = "\${llmagentbuilder.version}",
)

val llmDependencies = mapOf(
    "openai" to MavenCoordinate(artifactId = "llm-openai"),
)

val plannerDependencies = mapOf(
    "simple" to MavenCoordinate(artifactId = "agent-planner-simple"),
    "reAct" to MavenCoordinate(artifactId = "agent-planner-react"),
    "reActJson" to MavenCoordinate(artifactId = "agent-planner-react-json"),
)