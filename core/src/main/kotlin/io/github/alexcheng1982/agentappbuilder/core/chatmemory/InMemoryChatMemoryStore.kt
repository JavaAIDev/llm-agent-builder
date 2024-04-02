package io.github.alexcheng1982.agentappbuilder.core.chatmemory

import org.springframework.ai.chat.messages.Message
import java.util.concurrent.ConcurrentHashMap

class InMemoryChatMemoryStore : ChatMemoryStore {
    private val messagesByMemoryId = ConcurrentHashMap<String, List<Message>>()

    override fun getMessages(memoryId: String): List<Message> {
        return messagesByMemoryId.computeIfAbsent(memoryId) { listOf() }
    }

    override fun updateMessages(memoryId: String, messages: List<Message>) {
        messagesByMemoryId[memoryId] = messages
    }

    override fun deleteMessages(memoryId: String) {
        messagesByMemoryId.remove(memoryId)
    }
}