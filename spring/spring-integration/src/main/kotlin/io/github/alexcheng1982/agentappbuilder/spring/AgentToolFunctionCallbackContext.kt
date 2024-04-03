package io.github.alexcheng1982.agentappbuilder.spring

import io.github.alexcheng1982.agentappbuilder.core.AgentTools
import org.springframework.ai.model.function.FunctionCallback
import org.springframework.ai.model.function.FunctionCallbackContext

class AgentToolFunctionCallbackContext : FunctionCallbackContext() {

    override fun getFunctionCallback(
        beanName: String,
        defaultDescription: String?
    ): FunctionCallback {
        try {
            return super.getFunctionCallback(beanName, defaultDescription)
        } catch (e: Exception) {

        }
        return AgentTools.agentToolWrappers[beanName]
            ?: throw IllegalArgumentException("Function $beanName not found")
    }
}