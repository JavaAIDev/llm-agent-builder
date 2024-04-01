package cc.vividcode.ai.agentappbuilder.springai

import cc.vividcode.ai.agentappbuilder.core.AgentTool
import cc.vividcode.ai.agentappbuilder.core.AgentTools
import org.springframework.ai.model.function.FunctionCallback
import org.springframework.ai.model.function.FunctionCallbackContext
import org.springframework.ai.model.function.FunctionCallbackWrapper
import org.springframework.core.GenericTypeResolver

class FunctionCallbackContextAdapter : FunctionCallbackContext() {

    override fun getFunctionCallback(
        beanName: String,
        defaultDescription: String?
    ): FunctionCallback {
        try {
            return super.getFunctionCallback(beanName, defaultDescription)
        } catch (e: Exception) {

        }
        return AgentTools.agentTools[beanName]?.let { tool ->
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
        } ?: throw IllegalArgumentException("Function $beanName not found")
    }
}