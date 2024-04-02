package io.github.alexcheng1982.agentappbuilder.core.chatmemory

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage

class MessageWindowChatMemoryTest {
    @Test
    fun testMessagesOrder() {
        val memory = MessageWindowChatMemory(InMemoryChatMemoryStore(), "test")
        memory.add(UserMessage("1"))
        memory.add(SystemMessage("2"))
        memory.add(UserMessage("3"))
        memory.add(AssistantMessage("4"))
        val messages = memory.messages()
        assertEquals(4, messages.size)
        assertTrue(messages.first() is SystemMessage)
    }

    @Test
    fun testMessagesLimit() {
        val memory = MessageWindowChatMemory(InMemoryChatMemoryStore(), "test", 3)
        memory.add(UserMessage("1"))
        memory.add(SystemMessage("2"))
        memory.add(UserMessage("3"))
        memory.add(AssistantMessage("4"))
        val messages = memory.messages()
        assertEquals(3, messages.size)
        assertTrue(messages.first() is SystemMessage)
        assertEquals("3", messages[1].content)
    }
}