package io.github.llmagentbuilder.planner.structuredchat

import org.springframework.ai.chat.client.AdvisedRequest
import org.springframework.ai.chat.client.advisor.api.RequestAdvisor
import org.springframework.core.io.ClassPathResource
import java.nio.charset.Charset

class StructuredChatPromptAdvisor : RequestAdvisor {
    override fun getName(): String {
        return "StructuredChat Planner - Prompt"
    }

    override fun adviseRequest(
        request: AdvisedRequest,
        adviseContext: MutableMap<String, Any>
    ): AdvisedRequest {
        val systemParams = HashMap(request.systemParams ?: mapOf())
        systemParams["system_instruction"] = request.systemText ?: ""
        val systemText =
            ClassPathResource("/prompts/structured-chat/system.st").getContentAsString(
                Charset.defaultCharset()
            )
        val userParams = HashMap(request.userParams ?: mapOf())
        userParams["user_input"] = request.userText ?: ""
        val userText =
            ClassPathResource("/prompts/structured-chat/user.st").getContentAsString(
                Charset.defaultCharset()
            )
        return AdvisedRequest.from(request)
            .withSystemText(systemText)
            .withSystemParams(systemParams)
            .withUserText(userText)
            .withUserParams(userParams)
            .build()
    }
}