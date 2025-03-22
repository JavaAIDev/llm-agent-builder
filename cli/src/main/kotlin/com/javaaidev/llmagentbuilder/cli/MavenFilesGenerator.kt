package com.javaaidev.llmagentbuilder.cli

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import com.javaaidev.llmagentbuilder.core.VERSION
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import java.io.FileReader
import java.nio.file.Path
import java.util.*

object MavenFilesGenerator {
    private val handlebars = Handlebars(ClassPathTemplateLoader("/template"))

    fun generatePom(
        config: GenerationConfig,
        agentConfig: com.javaaidev.llmagentbuilder.core.AgentConfig
    ): String {
        val template = handlebars.compile("pom.xml")
        val pom = template.apply(
            mapOf(
                "groupId" to (config.groupId
                    ?: "com.javaaidev.llmagentbuilder.app"),
                "artifactId" to (config.artifactId
                    ?: "app_${UUID.randomUUID().toString().replace("-", "")}"),
                "springAiVersion" to (config.springAiVersion
                    ?: "1.0.0-M5"),
                "llmAgentBuilderVersion" to (config.llmAgentBuilderVersion
                    ?: VERSION),
                "dependencies" to collectDependencies(agentConfig),

                )
        )
        return pom

    }

    private fun collectDependencies(agentConfig: com.javaaidev.llmagentbuilder.core.AgentConfig): List<MavenCoordinate> {
        val plannerDeps =
            collectDependencies(agentConfig.planner, plannerDependencies)
        val llmDeps = collectDependencies(agentConfig.llm, llmDependencies)
        val toolDeps = agentConfig.tools?.mapNotNull {
            it.dependency?.let { dep ->
                MavenCoordinate(
                    dep.groupId,
                    dep.artifactId,
                    dep.version
                )
            }
        }?.toList() ?: listOf()
        return plannerDeps + llmDeps + toolDeps
    }

    private fun collectDependencies(
        subConfig: Map<String, Any?>?,
        allDeps: Map<String, MavenCoordinate>
    ): List<MavenCoordinate> {
        val deps = mutableListOf<MavenCoordinate>()
        subConfig?.let {
            it.entries.forEach { entry ->
                if ((entry.value as? Map<*, *>)?.get("enabled") == true) {
                    allDeps[entry.key]?.let { dep -> deps.add(dep) }
                }
            }
        }
        return deps.toList()
    }

}

fun main(args: Array<String>) {
    val configFile = Path.of(args.first())
    val config =
        Yaml(
            Constructor(
                com.javaaidev.llmagentbuilder.core.AgentConfig::class.java,
                LoaderOptions()
            )
        ).load<com.javaaidev.llmagentbuilder.core.AgentConfig>(
            FileReader(
                configFile.toFile()
            )
        )
    MavenFilesGenerator.generatePom(
        GenerationConfig(), config
    )
}