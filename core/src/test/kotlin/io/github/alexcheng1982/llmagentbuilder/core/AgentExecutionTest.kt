package io.github.alexcheng1982.llmagentbuilder.core

import io.github.llmagentbuilder.core.config.AgentConfig
import io.github.llmagentbuilder.core.config.ConfiguredAgentFactory
import io.github.llmagentbuilder.core.config.LLMConfig
import io.github.alexcheng1982.springai.dashscope.DashscopeChatClient
import io.github.llmagentbuilder.core.ChatAgentRequest
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

@Tag("native-image")
class AgentExecutionTest {
    @Test
    fun execute() {
        val chatClient = DashscopeChatClient.createDefault()
        val agentConfig = AgentConfig(LLMConfig(chatClient))
        val chatAgent = ConfiguredAgentFactory.createChatAgent(agentConfig)
        val response = chatAgent.call(ChatAgentRequest("hello"))
        assertNotNull(response.output)
    }
}