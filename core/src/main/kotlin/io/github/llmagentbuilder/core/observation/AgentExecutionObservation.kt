package io.github.llmagentbuilder.core.observation

import io.github.llmagentbuilder.core.ChatAgentRequest
import io.micrometer.common.KeyValue
import io.micrometer.common.KeyValues
import io.micrometer.common.docs.KeyName
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationConvention
import io.micrometer.observation.docs.ObservationDocumentation
import io.micrometer.observation.transport.RequestReplySenderContext


enum class AgentExecutionObservationDocumentation :
    ObservationDocumentation {
    AGENT_EXECUTION {
        override fun getDefaultConvention(): Class<out ObservationConvention<out Observation.Context>>? {
            return DefaultAgentExecutionObservationConvention::class.java
        }

        override fun getLowCardinalityKeyNames(): Array<KeyName> {
            return LowCardinalityKeyNames.values().toList().toTypedArray()
        }

        override fun getHighCardinalityKeyNames(): Array<KeyName> {
            return HighCardinalityKeyNames.values().toList().toTypedArray()
        }
    };

    enum class LowCardinalityKeyNames : KeyName {
        AGENT_NAME {
            override fun asString(): String {
                return "agent.name"
            }

        }
    }

    enum class HighCardinalityKeyNames : KeyName {
        AGENT_EXECUTION_INPUT {
            override fun asString(): String {
                return "agent.execution.input"
            }
        },
        AGENT_EXECUTION_OUTPUT {
            override fun asString(): String {
                return "agent.execution.output"
            }
        }
    }
}

class AgentExecutionObservationContext(
    val agentName: String,
    val input: ChatAgentRequest
) :
    RequestReplySenderContext<ChatAgentRequest, Map<String, Any>>({ _, _, _ ->
        run {}
    }) {
    init {
        setCarrier(input)
    }
}

interface AgentExecutionObservationConvention :
    ObservationConvention<AgentExecutionObservationContext> {
    override fun supportsContext(context: Observation.Context): Boolean {
        return context is AgentExecutionObservationContext
    }
}

class DefaultAgentExecutionObservationConvention(private val name: String? = null) :
    AgentExecutionObservationConvention {
    private val defaultName = "agent.execute"

    private val agentExecutionInputNone: KeyValue = KeyValue.of(
        AgentExecutionObservationDocumentation.HighCardinalityKeyNames.AGENT_EXECUTION_INPUT,
        KeyValue.NONE_VALUE
    )

    private val agentExecutionOutputNone: KeyValue = KeyValue.of(
        AgentExecutionObservationDocumentation.HighCardinalityKeyNames.AGENT_EXECUTION_OUTPUT,
        KeyValue.NONE_VALUE
    )

    override fun getName(): String {
        return name ?: defaultName
    }

    override fun getLowCardinalityKeyValues(context: AgentExecutionObservationContext): KeyValues {
        return KeyValues.of(agentName(context))
    }

    override fun getHighCardinalityKeyValues(context: AgentExecutionObservationContext): KeyValues {
        return KeyValues.of(
            agentExecutionInput(context),
            agentExecutionOutput(context)
        )
    }

    private fun agentName(context: AgentExecutionObservationContext): KeyValue {
        return KeyValue.of(
            AgentExecutionObservationDocumentation.LowCardinalityKeyNames.AGENT_NAME,
            context.agentName
        )
    }

    private fun agentExecutionInput(context: AgentExecutionObservationContext): KeyValue {
        return context.carrier?.let { input ->
            KeyValue.of(
                AgentExecutionObservationDocumentation.HighCardinalityKeyNames.AGENT_EXECUTION_INPUT,
                ObjectToJson.toJson(input)
            )
        } ?: agentExecutionInputNone
    }

    private fun agentExecutionOutput(context: AgentExecutionObservationContext): KeyValue {
        return context.response?.let { output ->
            KeyValue.of(
                AgentExecutionObservationDocumentation.HighCardinalityKeyNames.AGENT_EXECUTION_OUTPUT,
                ObjectToJson.toJson(output)
            )

        } ?: agentExecutionOutputNone
    }
}