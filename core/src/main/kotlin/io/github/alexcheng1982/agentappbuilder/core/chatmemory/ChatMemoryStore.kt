package io.github.alexcheng1982.agentappbuilder.core.chatmemory

import org.springframework.ai.chat.messages.Message

interface ChatMemoryStore {
    fun getMessages(memoryId: String): List<Message>

    fun updateMessages(memoryId: String, messages: List<Message>)

    fun deleteMessages(memoryId: String)
}