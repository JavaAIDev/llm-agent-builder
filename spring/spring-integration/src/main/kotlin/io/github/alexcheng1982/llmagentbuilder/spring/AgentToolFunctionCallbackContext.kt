package io.github.alexcheng1982.llmagentbuilder.spring

import io.github.alexcheng1982.llmagentbuilder.core.tool.AgentToolWrappersProvider
import io.github.alexcheng1982.llmagentbuilder.core.tool.AgentToolsProvider
import io.micrometer.observation.ObservationRegistry
import org.slf4j.LoggerFactory
import org.springframework.ai.model.function.FunctionCallback
import org.springframework.ai.model.function.FunctionCallbackContext

class AgentToolFunctionCallbackContext(
    agentToolsProvider: AgentToolsProvider,
    observationRegistry: ObservationRegistry? = null,
) :
    FunctionCallbackContext() {
    private val agentToolWrappersProvider =
        AgentToolWrappersProvider(agentToolsProvider, observationRegistry)

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun getFunctionCallback(
        beanName: String,
        defaultDescription: String?
    ): FunctionCallback {
        try {
            return super.getFunctionCallback(beanName, defaultDescription)
        } catch (e: Exception) {
            if (logger.isDebugEnabled) {
                logger.debug(
                    "Failed to get bean {} from application context, ignoring",
                    beanName
                )
            }
        }
        return agentToolWrappersProvider.get()[beanName]
            ?: throw IllegalArgumentException("Function $beanName not found")
    }
}