package io.github.alexcheng1982.agentappbuilder.core.chatmemory

import org.springframework.ai.chat.messages.Message

interface ChatMemory {
    fun id(): String
    fun add(message: Message)
    fun messages(): List<Message>
    fun clear()
}