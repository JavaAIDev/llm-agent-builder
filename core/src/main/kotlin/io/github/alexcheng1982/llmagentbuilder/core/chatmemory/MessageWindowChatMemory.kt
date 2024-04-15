package io.github.alexcheng1982.llmagentbuilder.core.chatmemory

import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.SystemMessage

class MessageWindowChatMemory(
    private val store: ChatMemoryStore,
    private val memoryId: String,
    private val maxMessages: Int = 10,
) : ChatMemory {
    override fun id(): String {
        return memoryId
    }

    override fun add(message: Message) {
        val messages = messages()
        val updated = messagesWithLimit((if (message is SystemMessage) {
            messages.filterNot { it is SystemMessage }
        } else messages) + listOf(message))
        store.updateMessages(memoryId, updated)
    }

    override fun messages(): List<Message> {
        val messages = store.getMessages(memoryId)
        return messagesWithLimit(messages.filterIsInstance<SystemMessage>() + messages.filterNot { it is SystemMessage })
    }

    private fun messagesWithLimit(messages: List<Message>): List<Message> {
        val limit = 1.coerceAtLeast(maxMessages)
        if (messages.size <= limit) {
            return messages
        }
        val systemMessages = messages.filterIsInstance<SystemMessage>()
        return systemMessages + messages.filterNot { it is SystemMessage }
            .takeLast((limit - systemMessages.size).coerceAtLeast(0))
    }

    override fun clear() {
        store.deleteMessages(memoryId)
    }
}