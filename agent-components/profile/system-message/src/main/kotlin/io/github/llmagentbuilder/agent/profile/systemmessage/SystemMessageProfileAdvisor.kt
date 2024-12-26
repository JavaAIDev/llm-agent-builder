package io.github.llmagentbuilder.agent.profile.systemmessage

import org.springframework.ai.chat.client.advisor.api.AdvisedRequest
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain
import org.springframework.core.Ordered

class SystemMessageProfileAdvisor(
    private val systemMessage: String,
    private val systemMessageParams: Map<String, Any>? = mapOf()
) : CallAroundAdvisor {
    override fun getName(): String {
        return javaClass.simpleName
    }

    override fun getOrder(): Int {
        return Ordered.HIGHEST_PRECEDENCE + 1000
    }

    override fun aroundCall(
        advisedRequest: AdvisedRequest,
        chain: CallAroundAdvisorChain
    ): AdvisedResponse {
        val systemParams =
            advisedRequest.systemParams + (systemMessageParams
                ?: mapOf())
        val request = AdvisedRequest.from(advisedRequest)
            .systemText(systemMessage)
            .systemParams(systemParams)
            .build()
        return chain.nextAroundCall(request)
    }
}