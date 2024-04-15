package io.github.alexcheng1982.llmagentbuilder.core.observation

import io.micrometer.common.KeyValue
import io.micrometer.common.KeyValues
import io.micrometer.common.docs.KeyName
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationConvention
import io.micrometer.observation.docs.ObservationDocumentation
import io.micrometer.observation.transport.RequestReplySenderContext


enum class AgentToolExecutionObservationDocumentation :
    ObservationDocumentation {
    AGENT_TOOL_EXECUTION {
        override fun getDefaultConvention(): Class<out ObservationConvention<out Observation.Context>>? {
            return DefaultAgentToolExecutionObservationConvention::class.java
        }

        override fun getLowCardinalityKeyNames(): Array<KeyName> {
            return LowCardinalityKeyNames.values().toList().toTypedArray()
        }

        override fun getHighCardinalityKeyNames(): Array<KeyName> {
            return HighCardinalityKeyNames.values().toList().toTypedArray()
        }
    };

    enum class LowCardinalityKeyNames : KeyName {
        TOOL_NAME {
            override fun asString(): String {
                return "agent.tool.name"
            }

        }
    }

    enum class HighCardinalityKeyNames : KeyName {
        TOOL_INPUT {
            override fun asString(): String {
                return "agent.tool.input"
            }
        },
        TOOL_OUTPUT {
            override fun asString(): String {
                return "agent.tool.output"
            }
        }
    }
}

class AgentToolExecutionObservationContext(
    val toolName: String,
    val input: String
) :
    RequestReplySenderContext<String, String>({ _, _, _ ->
        run {}
    }) {
    init {
        setCarrier(input)
    }
}

interface AgentToolExecutionObservationConvention :
    ObservationConvention<AgentToolExecutionObservationContext> {
    override fun supportsContext(context: Observation.Context): Boolean {
        return context is AgentToolExecutionObservationContext
    }
}

class DefaultAgentToolExecutionObservationConvention(private val name: String? = null) :
    AgentToolExecutionObservationConvention {
    private val defaultName = "agent.tool.execute"

    private val toolInputNone: KeyValue = KeyValue.of(
        AgentToolExecutionObservationDocumentation.HighCardinalityKeyNames.TOOL_INPUT,
        KeyValue.NONE_VALUE
    )

    private val toolOutputNone: KeyValue = KeyValue.of(
        AgentToolExecutionObservationDocumentation.HighCardinalityKeyNames.TOOL_OUTPUT,
        KeyValue.NONE_VALUE
    )

    override fun getName(): String {
        return name ?: defaultName
    }

    override fun getLowCardinalityKeyValues(context: AgentToolExecutionObservationContext): KeyValues {
        return KeyValues.of(toolName(context))
    }

    override fun getHighCardinalityKeyValues(context: AgentToolExecutionObservationContext): KeyValues {
        return KeyValues.of(toolInput(context), toolOutput(context))
    }

    private fun toolName(context: AgentToolExecutionObservationContext): KeyValue {
        return KeyValue.of(
            AgentToolExecutionObservationDocumentation.LowCardinalityKeyNames.TOOL_NAME,
            context.toolName
        )
    }

    private fun toolInput(context: AgentToolExecutionObservationContext): KeyValue {
        return context.carrier?.let { content ->
            KeyValue.of(
                AgentToolExecutionObservationDocumentation.HighCardinalityKeyNames.TOOL_INPUT,
                content
            )
        } ?: toolInputNone
    }

    private fun toolOutput(context: AgentToolExecutionObservationContext): KeyValue {
        return context.response?.let { output ->
            KeyValue.of(
                AgentToolExecutionObservationDocumentation.HighCardinalityKeyNames.TOOL_OUTPUT,
                output
            )

        } ?: toolOutputNone
    }
}