package io.github.alexcheng1982.agentappbuilder.core.tool

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.github.alexcheng1982.agentappbuilder.core.observation.AgentToolExecutionObservationContext
import io.github.alexcheng1982.agentappbuilder.core.observation.AgentToolExecutionObservationDocumentation
import io.github.alexcheng1982.agentappbuilder.core.observation.DefaultAgentToolExecutionObservationConvention
import io.micrometer.observation.ObservationRegistry
import org.slf4j.LoggerFactory
import org.springframework.ai.model.function.FunctionCallback
import org.springframework.ai.model.function.FunctionCallbackWrapper
import org.springframework.core.GenericTypeResolver
import java.util.*
import java.util.function.Supplier
import kotlin.streams.asSequence

interface AgentToolsProvider : Supplier<Map<String, AgentTool<*, *>>>

class AgentToolWrappersProvider(
    private val agentToolsProvider: AgentToolsProvider,
    private val observationRegistry: ObservationRegistry? = null
) :
    Supplier<Map<String, FunctionCallback>> {
    override fun get(): Map<String, FunctionCallback> {
        val objectMapper =
            ObjectMapper().registerModule(KotlinModule.Builder().build())
        return agentToolsProvider.get().mapValues { (_, tool) ->
            val types =
                GenericTypeResolver.resolveTypeArguments(
                    tool.javaClass,
                    AgentTool::class.java
                )
            InstrumentedFunctionCallbackWrapper(
                FunctionCallbackWrapper.builder(tool)
                    .withName(tool.name())
                    .withSchemaType(FunctionCallbackWrapper.Builder.SchemaType.JSON_SCHEMA)
                    .withDescription(tool.description())
                    .withInputType(
                        types?.get(0)
                            ?: throw IllegalArgumentException("Bad type")
                    )
                    .withObjectMapper(objectMapper)
                    .build(), observationRegistry
            )
        }
    }

    private class InstrumentedFunctionCallbackWrapper<I, O>(
        private val functionCallbackWrapper: FunctionCallbackWrapper<I, O>,
        private val observationRegistry: ObservationRegistry? = null
    ) :
        FunctionCallback {
        override fun getName(): String {
            return functionCallbackWrapper.name
        }

        override fun getDescription(): String {
            return functionCallbackWrapper.description
        }

        override fun getInputTypeSchema(): String {
            return functionCallbackWrapper.inputTypeSchema
        }

        override fun call(functionInput: String): String {
            val action = { functionCallbackWrapper.call(functionInput) }
            return observationRegistry?.let { registry ->
                instrumentedCall(functionInput, action, registry)
            } ?: action.invoke()
        }

        private fun instrumentedCall(
            input: String,
            action: () -> String,
            registry: ObservationRegistry
        ): String {
            val observationContext =
                AgentToolExecutionObservationContext(name, input)
            val observation =
                AgentToolExecutionObservationDocumentation.AGENT_TOOL_EXECUTION.observation(
                    null,
                    DefaultAgentToolExecutionObservationConvention(),
                    { observationContext },
                    registry
                ).start()
            return try {
                observation.openScope().use {
                    val response = action.invoke()
                    observationContext.setResponse(response)
                    response
                }
            } catch (e: Exception) {
                observation.error(e)
                throw e
            } finally {
                observation.stop()
            }
        }
    }

}

class CompositeAgentToolsProvider(private val providers: List<AgentToolsProvider>) :
    AgentToolsProvider {
    override fun get(): Map<String, AgentTool<*, *>> {
        return providers.flatMap { it.get().values }
            .distinctBy { it.name() }
            .associateBy { it.name() }
    }
}

object AutoDiscoveredAgentToolsProvider : AgentToolsProvider {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val agentTools: Map<String, AgentTool<*, *>> by lazy {
        ServiceLoader.load(AgentToolFactory::class.java)
            .stream()
            .map { it.get() }
            .map { it.create() }
            .asSequence()
            .associateBy { it.name() }.also {
                logger.info("Found agent tools {}", it.keys)
            }
    }

    override fun get(): Map<String, AgentTool<*, *>> {
        return agentTools
    }
}