package io.github.alexcheng1982.llmagentbuilder.core.config

import io.github.alexcheng1982.llmagentbuilder.core.Planner
import io.github.alexcheng1982.llmagentbuilder.core.chatmemory.ChatMemoryStore
import io.github.alexcheng1982.llmagentbuilder.core.planner.react.ReActPlannerFactory
import io.github.alexcheng1982.llmagentbuilder.core.planner.reactjson.ReActJsonPlannerFactory
import io.github.alexcheng1982.llmagentbuilder.core.planner.simple.SimplePlannerFactory
import io.github.alexcheng1982.llmagentbuilder.core.planner.structuredchat.StructuredChatPlannerFactory
import io.github.alexcheng1982.llmagentbuilder.core.tool.AgentToolsProvider
import io.github.alexcheng1982.llmagentbuilder.core.tool.AutoDiscoveredAgentToolsProvider
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.observation.ObservationRegistry
import org.springframework.ai.chat.ChatClient

enum class PlannerType {
    ReAct {
        override fun create(agentConfig: AgentConfig): Planner {
            return ReActPlannerFactory.create(agentConfig)
        }
    },
    ReActJson {
        override fun create(agentConfig: AgentConfig): Planner {
            return ReActJsonPlannerFactory.create(agentConfig)
        }
    },
    StructuredChat {
        override fun create(agentConfig: AgentConfig): Planner {
            return StructuredChatPlannerFactory.create(agentConfig)
        }
    },
    Simple {
        override fun create(agentConfig: AgentConfig): Planner {
            return SimplePlannerFactory.create(agentConfig)
        }
    };

    abstract fun create(agentConfig: AgentConfig): Planner
}

data class MetadataConfig(
    val name: String = "ChatAgent",
    val description: String = "A conversational chat agent",
    val id: String? = null,
    val usageInstruction: String? = null,
)

data class LLMConfig(
    val chatClient: ChatClient,
)

data class PlannerConfig(
    val plannerType: PlannerType = PlannerType.Simple,
    val systemInstruction: String? = null,
)

data class ToolsConfig(
    val agentToolsProvider: AgentToolsProvider? = AutoDiscoveredAgentToolsProvider,
)

data class MemoryConfig(
    val chatMemoryStore: ChatMemoryStore? = null,
)

data class ObservationConfig(
    val observationRegistry: ObservationRegistry? = null,
    val meterRegistry: MeterRegistry? = null,
)

class AgentConfig(
    val llmConfig: LLMConfig,
    val metadataConfig: MetadataConfig? = null,
    val plannerConfig: PlannerConfig? = null,
    val toolsConfig: ToolsConfig? = null,
    val memoryConfig: MemoryConfig? = null,
    val observationConfig: ObservationConfig? = null,
) {
    fun metadataConfig() = metadataConfig ?: MetadataConfig()
    fun plannerConfig() = plannerConfig ?: PlannerConfig()
    fun toolsConfig() = toolsConfig ?: ToolsConfig()
    fun memoryConfig() = memoryConfig ?: MemoryConfig()
    fun observationConfig() = observationConfig ?: ObservationConfig()
}