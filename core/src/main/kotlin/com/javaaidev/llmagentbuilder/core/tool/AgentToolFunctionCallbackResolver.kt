package com.javaaidev.llmagentbuilder.core.tool

import io.micrometer.observation.ObservationRegistry
import org.slf4j.LoggerFactory
import org.springframework.ai.model.function.DefaultFunctionCallbackResolver
import org.springframework.ai.model.function.FunctionCallback

class AgentToolFunctionCallbackResolver(
    agentToolsProvider: AgentToolsProvider,
    observationRegistry: ObservationRegistry? = null,
) :
    DefaultFunctionCallbackResolver() {
    private val agentToolWrappersProvider =
        AgentToolWrappersProvider(agentToolsProvider, observationRegistry)

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun resolve(name: String): FunctionCallback {
        try {
            return super.resolve(name)
        } catch (e: Exception) {
            if (logger.isDebugEnabled) {
                logger.debug(
                    "Failed to get bean {} from application context, ignoring",
                    name
                )
            }
        }
        return agentToolWrappersProvider.get()[name]
            ?: throw IllegalArgumentException("Function $name not found")
    }
}