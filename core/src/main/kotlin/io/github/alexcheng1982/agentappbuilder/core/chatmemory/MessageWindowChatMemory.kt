package io.github.alexcheng1982.agentappbuilder.core.chatmemory

import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.SystemMessage

class MessageWindowChatMemory(
    private val store: ChatMemoryStore,
    private val memoryId: String
) : ChatMemory {
    override fun id(): String {
        return memoryId
    }

    override fun add(message: Message) {
        val messages = messages()
        val updated = (if (message is SystemMessage) {
            messages.filterNot { it is SystemMessage }
        } else messages) + listOf(message)
        store.updateMessages(memoryId, updated)
    }

    override fun messages(): List<Message> {
        val messages = store.getMessages(memoryId)
        return messages.filterIsInstance<SystemMessage>() + messages.filterNot { it is SystemMessage }
    }

    override fun clear() {
        store.deleteMessages(memoryId)
    }
}