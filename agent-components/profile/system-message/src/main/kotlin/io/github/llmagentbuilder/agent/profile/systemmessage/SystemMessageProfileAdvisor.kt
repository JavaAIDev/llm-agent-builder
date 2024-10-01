package io.github.llmagentbuilder.agent.profile.systemmessage

import org.springframework.ai.chat.client.AdvisedRequest
import org.springframework.ai.chat.client.advisor.api.RequestAdvisor

class SystemMessageProfileAdvisor(
    private val systemMessage: String,
    private val systemMessageParams: Map<String, Any>? = mapOf()
) : RequestAdvisor {
    override fun getName(): String {
        return "Profile - System Message"
    }

    override fun adviseRequest(
        request: AdvisedRequest,
        adviseContext: MutableMap<String, Any>
    ): AdvisedRequest {
        val systemParams =
            (request.systemParams ?: mapOf()) + (systemMessageParams ?: mapOf())
        return AdvisedRequest.from(request)
            .withSystemText(systemMessage)
            .withSystemParams(systemParams)
            .build()
    }
}