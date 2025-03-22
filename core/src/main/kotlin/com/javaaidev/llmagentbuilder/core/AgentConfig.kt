package com.javaaidev.llmagentbuilder.core

import com.github.jknack.handlebars.Handlebars
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import java.io.*

class ProfileConfig {
    var system: String? = null
}

class InMemoryMemoryConfig {
    var enabled: Boolean? = true
}

class MemoryConfig {
    var inMemory: com.javaaidev.llmagentbuilder.core.InMemoryMemoryConfig? = null
}

class ToolConfig {
    lateinit var id: String
    var dependency: com.javaaidev.llmagentbuilder.core.ToolDependency? = null
    var config: Map<String, Any?>? = null
}

class ToolDependency {
    lateinit var groupId: String
    lateinit var artifactId: String
    lateinit var version: String
}

class AgentMetadata {
    var id: String? = null
    var name: String? = null
    var description: String? = null
    var usageInstruction: String? = null
}

class TracingExporterConfig {
    lateinit var endpoint: String
    var headers: Map<String, String>? = null
}

class TracingConfig {
    var enabled: Boolean? = false
    var exporter: com.javaaidev.llmagentbuilder.core.TracingExporterConfig? = null
}

class MetricsExporterConfig {
    lateinit var endpoint: String
    var headers: Map<String, String>? = null
}

class MetricsConfig {
    var enabled: Boolean? = false
    var exporter: com.javaaidev.llmagentbuilder.core.MetricsExporterConfig? = null
}

class ObservationConfig {
    var enabled: Boolean? = false
    var tracing: com.javaaidev.llmagentbuilder.core.TracingConfig? = null
    var metrics: com.javaaidev.llmagentbuilder.core.MetricsConfig? = null
}

class LaunchConfig {
    var server: com.javaaidev.llmagentbuilder.core.ServerConfig? = null
    val feature: com.javaaidev.llmagentbuilder.core.FeatureConfig? = null
}

class ServerConfig {
    var host: String? = "localhost"
    var port: Int? = 8080
}

class FeatureConfig {
    var devUiEnabled: Boolean? = true
}


class AgentConfig {
    var metadata: com.javaaidev.llmagentbuilder.core.AgentMetadata? = null
    var profile: com.javaaidev.llmagentbuilder.core.ProfileConfig? = null
    var memory: com.javaaidev.llmagentbuilder.core.MemoryConfig? = null
    var llm: Map<String, Any?>? = null
    var planner: Map<String, Any?>? = null
    var tools: List<com.javaaidev.llmagentbuilder.core.ToolConfig>? = null
    var observation: com.javaaidev.llmagentbuilder.core.ObservationConfig? = null
    var launch: com.javaaidev.llmagentbuilder.core.LaunchConfig? = null
}

object AgentConfigLoader {
    fun load(configFile: File): com.javaaidev.llmagentbuilder.core.AgentConfig {
        return com.javaaidev.llmagentbuilder.core.AgentConfigLoader.load(FileReader(configFile))
    }

    fun load(configFileStream: InputStream): com.javaaidev.llmagentbuilder.core.AgentConfig {
        return com.javaaidev.llmagentbuilder.core.AgentConfigLoader.load(
            InputStreamReader(
                configFileStream
            )
        )
    }

    private fun load(reader: Reader): com.javaaidev.llmagentbuilder.core.AgentConfig {
        return Yaml(
            Constructor(
                com.javaaidev.llmagentbuilder.core.AgentConfig::class.java,
                LoaderOptions()
            )
        ).load(reader)
    }
}

object EvaluationHelper {
    private val handlebars = Handlebars()

    fun evaluate(input: String): String {
        return com.javaaidev.llmagentbuilder.core.EvaluationHelper.handlebars.compileInline(input)
            .apply(
                mapOf(
                    "env" to System.getenv(),
                )
            )
    }
}