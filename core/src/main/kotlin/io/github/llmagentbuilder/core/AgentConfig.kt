package io.github.llmagentbuilder.core

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
    var inMemory: InMemoryMemoryConfig? = null
}

class ToolConfig {
    lateinit var id: String
    var dependency: ToolDependency? = null
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
    var exporter: TracingExporterConfig? = null
}

class MetricsExporterConfig {
    lateinit var endpoint: String
    var headers: Map<String, String>? = null
}

class MetricsConfig {
    var enabled: Boolean? = false
    var exporter: MetricsExporterConfig? = null
}

class ObservationConfig {
    var enabled: Boolean? = false
    var tracing: TracingConfig? = null
    var metrics: MetricsConfig? = null
}

class AgentConfig {
    var metadata: AgentMetadata? = null
    var profile: ProfileConfig? = null
    var memory: MemoryConfig? = null
    var llm: Map<String, Any?>? = null
    var planner: Map<String, Any?>? = null
    var tools: List<ToolConfig>? = null
    var observation: ObservationConfig? = null
}

object AgentConfigLoader {
    fun load(configFile: File): AgentConfig {
        return load(FileReader(configFile))
    }

    fun load(configFileStream: InputStream): AgentConfig {
        return load(InputStreamReader(configFileStream))
    }

    private fun load(reader: Reader): AgentConfig {
        return Yaml(
            Constructor(
                AgentConfig::class.java,
                LoaderOptions()
            )
        ).load(reader)
    }
}

object EvaluationHelper {
    private val handlebars = Handlebars()

    fun evaluate(input: String): String {
        return handlebars.compileInline(input).apply(
            mapOf(
                "env" to System.getenv(),
            )
        )
    }
}