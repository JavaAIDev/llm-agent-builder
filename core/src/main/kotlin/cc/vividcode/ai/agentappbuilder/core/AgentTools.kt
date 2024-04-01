package cc.vividcode.ai.agentappbuilder.core

import org.springframework.ai.model.function.FunctionCallbackWrapper
import org.springframework.core.GenericTypeResolver
import java.util.*
import kotlin.streams.asSequence

object AgentTools {
    val agentTools: Map<String, AgentTool<*, *>> by lazy {
        ServiceLoader.load(AgentToolFactory::class.java)
            .stream()
            .map { it.get() }
            .map { it.create() }
            .asSequence()
            .associateBy { it.name() }
    }

    val agentToolWrappers : Map<String, FunctionCallbackWrapper<*, *>> by lazy {
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
                .build()
        }
    }
}