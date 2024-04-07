package io.github.alexcheng1982.agentappbuilder.core.observation

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.alexcheng1982.agentappbuilder.core.executor.ActionPlanningResult
import io.micrometer.common.KeyValue
import io.micrometer.common.KeyValues
import io.micrometer.common.docs.KeyName
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationConvention
import io.micrometer.observation.docs.ObservationDocumentation
import io.micrometer.observation.transport.RequestReplySenderContext
import java.util.*


enum class AgentPlanningObservationDocumentation : ObservationDocumentation {
    AGENT_PLANNING {
        override fun getDefaultConvention(): Class<out ObservationConvention<out Observation.Context>>? {
            return DefaultAgentPlanningObservationConvention::class.java
        }

        override fun getLowCardinalityKeyNames(): Array<KeyName> {
            return LowCardinalityKeyNames.values().toList().toTypedArray()
        }

        override fun getHighCardinalityKeyNames(): Array<KeyName> {
            return HighCardinalityKeyNames.values().toList().toTypedArray()
        }
    };

    enum class LowCardinalityKeyNames : KeyName

    enum class HighCardinalityKeyNames : KeyName {
        PLANNING_INPUT {
            override fun asString(): String {
                return "agent.planning.input"
            }
        },
        PLANNING_RESULT {
            override fun asString(): String {
                return "agent.planning.result"
            }
        }
    }
}

class AgentPlanningObservationContext(val input: Map<String, Any>) :
    RequestReplySenderContext<Map<String, Any>, ActionPlanningResult>({ _, _, _ ->
        run {}
    }) {
    init {
        setCarrier(input)
    }
}

interface AgentPlanningObservationConvention :
    ObservationConvention<AgentPlanningObservationContext> {
    override fun supportsContext(context: Observation.Context): Boolean {
        return context is AgentPlanningObservationContext
    }
}

class DefaultAgentPlanningObservationConvention(private val name: String? = null) :
    AgentPlanningObservationConvention {
    private val defaultName = "agent.plan"

    private val planningInputNone: KeyValue = KeyValue.of(
        AgentPlanningObservationDocumentation.HighCardinalityKeyNames.PLANNING_INPUT,
        KeyValue.NONE_VALUE
    )

    private val planningResultNone: KeyValue = KeyValue.of(
        AgentPlanningObservationDocumentation.HighCardinalityKeyNames.PLANNING_RESULT,
        KeyValue.NONE_VALUE
    )

    override fun getName(): String {
        return name ?: defaultName
    }

    override fun getLowCardinalityKeyValues(context: AgentPlanningObservationContext): KeyValues {
        return KeyValues.empty()
    }

    override fun getHighCardinalityKeyValues(context: AgentPlanningObservationContext): KeyValues {
        return KeyValues.of(planningInput(context), planningResult(context))
    }

    private fun planningInput(context: AgentPlanningObservationContext): KeyValue {
        return context.carrier?.let { input ->
            KeyValue.of(
                AgentPlanningObservationDocumentation.HighCardinalityKeyNames.PLANNING_INPUT,
                ObjectToJson.toJson(input)
            )
        } ?: planningInputNone
    }

    private fun planningResult(context: AgentPlanningObservationContext): KeyValue {
        return context.response?.let { result ->
            KeyValue.of(
                AgentPlanningObservationDocumentation.HighCardinalityKeyNames.PLANNING_RESULT,
                ObjectToJson.toJson(result)
            )

        } ?: planningResultNone
    }
}

internal object ObjectToJson {
    private val objectMapper = ObjectMapper()
    fun toJson(input: Any): String {
        return try {
            objectMapper.writeValueAsString(input)
        } catch (e: Exception) {
            Objects.toString(input)
        }
    }
}