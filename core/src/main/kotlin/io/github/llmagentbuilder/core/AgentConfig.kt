package io.github.llmagentbuilder.core

import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import java.io.File
import java.io.FileReader

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

class AgentConfig {
    var metadata: AgentMetadata? = null
    var profile: ProfileConfig? = null
    var memory: MemoryConfig? = null
    var llm: Map<String, Any?>? = null
    var planner: Map<String, Any?>? = null
    var tools: List<ToolConfig>? = null
}

object AgentConfigLoader {
    fun load(configFile: File): AgentConfig {
        return Yaml(
            Constructor(
                AgentConfig::class.java,
                LoaderOptions()
            )
        ).load(FileReader(configFile))
    }
}