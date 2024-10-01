package io.github.llmagentbuilder.planner.reactjson

import org.springframework.ai.chat.client.AdvisedRequest
import org.springframework.ai.chat.client.advisor.api.RequestAdvisor
import org.springframework.core.io.ClassPathResource
import java.nio.charset.Charset

class ReActJsonPromptAdvisor : RequestAdvisor {
    override fun getName(): String {
        return "ReAct Json Planner - Prompt"
    }

    override fun adviseRequest(
        request: AdvisedRequest,
        adviseContext: MutableMap<String, Any>
    ): AdvisedRequest {
        val systemParams = HashMap(request.systemParams ?: mapOf())
        systemParams["system_instruction"] = request.systemText ?: ""
        val systemText =
            ClassPathResource("prompts/react-json/system.st").getContentAsString(
                Charset.defaultCharset()
            )
        return AdvisedRequest.from(request)
            .withSystemText(systemText)
            .withSystemParams(systemParams)
            .build()
    }
}