package io.github.alexcheng1982.agentappbuilder.example

import cc.vividcode.ai.agent.dashscope.DashscopeChatClient
import cc.vividcode.ai.agent.dashscope.DashscopeChatOptions
import cc.vividcode.ai.agent.dashscope.api.DashscopeApi
import cc.vividcode.ai.agent.dashscope.api.DashscopeModelName
import io.github.alexcheng1982.agentappbuilder.core.AgentFactory
import io.github.alexcheng1982.agentappbuilder.core.ChatAgent
import io.github.alexcheng1982.agentappbuilder.core.chatmemory.ChatMemoryStore
import io.github.alexcheng1982.agentappbuilder.core.chatmemory.InMemoryChatMemoryStore
import io.github.alexcheng1982.agentappbuilder.core.chatmemory.MessageWindowChatMemory
import io.github.alexcheng1982.agentappbuilder.core.planner.reactjson.ReactJsonPlanner
import io.github.alexcheng1982.agentappbuilder.springai.FunctionCallbackContextAdapter
import org.springframework.ai.chat.ChatClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.io.InputStreamReader
import java.util.*

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
    ): ChatAgent {
        val instructions =
            ClassPathResource("chinese-idioms.txt").inputStream.use {
                InputStreamReader(it).readText()
            }
        return AgentFactory.createChatAgent(
            ReactJsonPlanner.createDefault(
                chatClient,
                instructions,
                MessageWindowChatMemory(
                    chatMemoryStore,
                    UUID.randomUUID().toString()
                ),
            )
        )
    }

    @Bean
    fun agentService(agent: ChatAgent): AgentService {
        return AgentService(agent)
    }

    @Bean
    fun functionCallbackContext(): FunctionCallbackContextAdapter {
        return FunctionCallbackContextAdapter()
    }
}