package cc.vividcode.ai.agentappbuilder.core

import cc.vividcode.ai.agentappbuilder.core.executor.AgentExecutor

object AgentFactory {
    fun <REQUEST : AgentRequest, RESPONSE> create(
        name: String,
        description: String,
        planner: Planner,
        responseFactory: (Map<String, Any>) -> RESPONSE
    ): Agent<REQUEST, RESPONSE> {
        val executor = AgentExecutor(planner, AgentTools.agentToolWrappers)
        return object : Agent<REQUEST, RESPONSE> {
            override fun name() = name

            override fun description() = description

            override fun call(request: REQUEST): RESPONSE {
                return responseFactory(executor.call(request.toMap()))
            }
        }
    }
}