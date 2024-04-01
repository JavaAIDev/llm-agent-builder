package cc.vividcode.ai.agentappbuilder.core

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
}