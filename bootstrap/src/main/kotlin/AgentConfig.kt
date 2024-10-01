data class LlmConfig(
    val ollama: OllamaConfig? = null,
)

data class OllamaConfig(
    val model: String,
    val baseUrl: String = "http://localhost:11434",
    val temperature: Double? = null,
)

data class ProfileConfig(
    val system: String? = null,
)

data class InMemoryMemoryConfig(
    val enabled: Boolean? = true,
)

data class MemoryConfig(
    val inMemory: InMemoryMemoryConfig? = null,
)

data class ReActConfig(
    val enabled: Boolean? = true,
)

data class PlannerConfig(
    val reAct: ReActConfig? = null,
)

data class AgentConfig(
    val profile: ProfileConfig? = null,
    val memory: MemoryConfig? = null,
    val planner: PlannerConfig? = null,
)