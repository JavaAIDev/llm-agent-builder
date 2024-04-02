package io.github.alexcheng1982.agentappbuilder.example

import cc.vividcode.ai.agent.dashscope.DashscopeChatClient
import cc.vividcode.ai.agent.dashscope.DashscopeChatOptions
import cc.vividcode.ai.agent.dashscope.api.DashscopeApi
import cc.vividcode.ai.agent.dashscope.api.DashscopeModelName
import io.github.alexcheng1982.agentappbuilder.core.Agent
import io.github.alexcheng1982.agentappbuilder.core.AgentFactory
import io.github.alexcheng1982.agentappbuilder.core.planner.reactjson.ReactJsonPlanner
import io.github.alexcheng1982.agentappbuilder.springai.FunctionCallbackContextAdapter
import org.springframework.ai.chat.ChatClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DemoConfiguration {

    @Bean
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
    fun agent(chatClient: ChatClient): Agent<MathAgentRequest, MathAgentResponse> {
        return AgentFactory.create(
            "math",
            "Do basic math",
            ReactJsonPlanner.createDefault(chatClient)
        ) { output -> MathAgentResponse((output["output"] ?: "").toString()) }
    }

    @Bean
    fun agentService(agent: Agent<MathAgentRequest, MathAgentResponse>): AgentService {
        return AgentService(agent)
    }

    @Bean
    fun functionCallbackContext(): FunctionCallbackContextAdapter {
        return FunctionCallbackContextAdapter()
    }
}