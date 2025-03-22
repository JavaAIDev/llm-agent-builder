package com.javaaidev.llmagentbuilder.cli

data class GenerationConfig(
    val groupId: String? = null,
    val artifactId: String? = null,
    val springAiVersion: String? = null,
    val llmAgentBuilderVersion: String? = null,
)