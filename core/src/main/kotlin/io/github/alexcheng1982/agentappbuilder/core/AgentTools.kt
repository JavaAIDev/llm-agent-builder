package io.github.alexcheng1982.agentappbuilder.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.slf4j.LoggerFactory
import org.springframework.ai.model.function.FunctionCallbackWrapper
import org.springframework.core.GenericTypeResolver
import java.util.*
import kotlin.streams.asSequence

object AgentTools {
    private val logger = LoggerFactory.getLogger(javaClass)
    val agentTools: Map<String, AgentTool<*, *>> by lazy {
        ServiceLoader.load(AgentToolFactory::class.java)
            .stream()
            .map { it.get() }
            .map { it.create() }
            .asSequence()
            .associateBy { it.name() }.also {
                logger.info("Found following tools {}", it.keys)
            }
    }

    val agentToolWrappers: Map<String, FunctionCallbackWrapper<*, *>> by lazy {
        val objectMapper =
            ObjectMapper().registerModule(KotlinModule.Builder().build())
        agentTools.mapValues { (_, tool) ->
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