package io.github.alexcheng1982.agentappbuilder.example

import cc.vividcode.ai.agent.dashscope.DashscopeChatClient
import cc.vividcode.ai.agent.dashscope.DashscopeChatOptions
import cc.vividcode.ai.agent.dashscope.api.DashscopeApi
import cc.vividcode.ai.agent.dashscope.api.DashscopeModelName
import io.github.alexcheng1982.agentappbuilder.spring.FunctionCallbackContextAdapter
import org.springframework.ai.chat.ChatClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class DemoConfiguration {

    @Bean
    @Primary
    fun chatClient(functionCallbackContext: FunctionCallbackContextAdapter): ChatClient {
        return DashscopeChatClient(
            DashscopeApi(),
            DashscopeChatOptions.builder()
                .withModel(DashscopeModelName.QWEN_MAX)
                .withTemperature(0.2f)
                .build(),
            functionCallbackContext
        )
    }

    @Bean
    fun functionCallbackContext(): FunctionCallbackContextAdapter {
        return FunctionCallbackContextAdapter()
    }
}