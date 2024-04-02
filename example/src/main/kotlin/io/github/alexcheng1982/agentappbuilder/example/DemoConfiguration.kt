package io.github.alexcheng1982.agentappbuilder.example

import cc.vividcode.ai.agent.dashscope.DashscopeChatClient
import cc.vividcode.ai.agent.dashscope.DashscopeChatOptions
import cc.vividcode.ai.agent.dashscope.api.DashscopeApi
import cc.vividcode.ai.agent.dashscope.api.DashscopeModelName
import io.github.alexcheng1982.agentappbuilder.core.Agent
import io.github.alexcheng1982.agentappbuilder.core.AgentFactory
import io.github.alexcheng1982.agentappbuilder.core.ChatAgentRequest
import io.github.alexcheng1982.agentappbuilder.core.ChatAgentResponse
import io.github.alexcheng1982.agentappbuilder.core.chatmemory.ChatMemoryStore
import io.github.alexcheng1982.agentappbuilder.core.chatmemory.InMemoryChatMemoryStore
import io.github.alexcheng1982.agentappbuilder.core.chatmemory.MessageWindowChatMemory
import io.github.alexcheng1982.agentappbuilder.core.planner.reactjson.ReactJsonPlanner
import io.github.alexcheng1982.agentappbuilder.springai.FunctionCallbackContextAdapter
import org.springframework.ai.chat.ChatClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource

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
    fun chatMemoryStore(): ChatMemoryStore {
        return InMemoryChatMemoryStore()
    }

    @Bean
    fun agent(
        chatClient: ChatClient,
        chatMemoryStore: ChatMemoryStore
    ): Agent<ChatAgentRequest, ChatAgentResponse> {
        return AgentFactory.createChatAgent(
            "chat",
            "Basic chat service",
            ReactJsonPlanner.createDefault(
                chatClient,
                MessageWindowChatMemory(chatMemoryStore, "demo"),
                ClassPathResource("prompts/chinese-idioms/user.st"),
                ClassPathResource("prompts/chinese-idioms/system.st"),
            )
        )
    }

    @Bean
    fun agentService(agent: Agent<ChatAgentRequest, ChatAgentResponse>): AgentService {
        return AgentService(agent)
    }

    @Bean
    fun functionCallbackContext(): FunctionCallbackContextAdapter {
        return FunctionCallbackContextAdapter()
    }
}