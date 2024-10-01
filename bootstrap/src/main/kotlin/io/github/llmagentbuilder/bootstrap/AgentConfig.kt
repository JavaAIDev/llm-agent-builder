package io.github.llmagentbuilder.bootstrap

class ProfileConfig {
    var system: String? = null
}

class InMemoryMemoryConfig {
    var enabled: Boolean? = true
}

class MemoryConfig {
    var inMemory: InMemoryMemoryConfig? = null
}

class ToolDependency {
    lateinit var groupId: String
    lateinit var artifactId: String
    lateinit var version: String
}

class AgentConfig {
    var profile: ProfileConfig? = null
    var memory: MemoryConfig? = null
    var llm: Map<String, Any?>? = null
    var planner: Map<String, Any?>? = null
    var tools: List<ToolDependency>? = null
}