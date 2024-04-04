package io.github.alexcheng1982.agentappbuilder.spring

import io.github.alexcheng1982.agentappbuilder.core.tool.AgentToolWrappersProvider
import io.github.alexcheng1982.agentappbuilder.core.tool.AgentToolsProvider
import org.slf4j.LoggerFactory
import org.springframework.ai.model.function.FunctionCallback
import org.springframework.ai.model.function.FunctionCallbackContext

class AgentToolFunctionCallbackContext(
    agentToolsProvider: AgentToolsProvider,
) :
    FunctionCallbackContext() {
    private val agentToolWrappersProvider =
        AgentToolWrappersProvider(agentToolsProvider)

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