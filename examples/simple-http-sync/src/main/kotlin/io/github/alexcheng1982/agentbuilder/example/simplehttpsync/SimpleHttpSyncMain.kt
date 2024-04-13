package io.github.alexcheng1982.agentbuilder.example.simplehttpsync

import io.github.alexcheng1982.agentappbuilder.core.config.AgentConfig
import io.github.alexcheng1982.agentappbuilder.core.config.LLMConfig
import io.github.alexcheng1982.agentbuilder.launcher.jdkhttpsync.JdkHttpSyncLauncher
import io.github.alexcheng1982.springai.dashscope.DashscopeChatClient

fun main() {
    val chatClient = DashscopeChatClient.createDefault()
    val agentConfig = AgentConfig(LLMConfig(chatClient))
    JdkHttpSyncLauncher().launch(agentConfig)
}