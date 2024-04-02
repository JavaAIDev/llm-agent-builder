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
                "你和用户玩成语接龙的游戏。对于用户输入的成语，你首先检查是否为合法的成语。如果不是，告知用户。如果是的话，对于用户输入的成语，给出一个以输入的成语的最后一个字作为起始的成语。只输出该成语本身。",
                MessageWindowChatMemory(chatMemoryStore, "demo"),
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