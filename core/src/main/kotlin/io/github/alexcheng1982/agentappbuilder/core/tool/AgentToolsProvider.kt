package io.github.alexcheng1982.agentappbuilder.core.tool

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.slf4j.LoggerFactory
import org.springframework.ai.model.function.FunctionCallbackWrapper
import org.springframework.core.GenericTypeResolver
import java.util.*
import java.util.function.Supplier
import kotlin.streams.asSequence

interface AgentToolsProvider : Supplier<Map<String, AgentTool<*, *>>>

class AgentToolWrappersProvider(private val agentToolsProvider: AgentToolsProvider) :
    Supplier<Map<String, FunctionCallbackWrapper<*, *>>> {
    override fun get(): Map<String, FunctionCallbackWrapper<*, *>> {
        val objectMapper =
            ObjectMapper().registerModule(KotlinModule.Builder().build())
        return agentToolsProvider.get().mapValues { (_, tool) ->
            val types =
                GenericTypeResolver.resolveTypeArguments(
                    tool.javaClass,
                    AgentTool::class.java
                )
            FunctionCallbackWrapper.builder(tool)
                .withName(tool.name())
                .withSchemaType(FunctionCallbackWrapper.Builder.SchemaType.JSON_SCHEMA)
                .withDescription(tool.description())
                .withInputType(
                    types?.get(0) ?: throw IllegalArgumentException("Bad type")
                )
                .withObjectMapper(objectMapper)
                .build()
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