package io.github.llmagentbuilder.core.chatmemory

import org.springframework.ai.chat.messages.Message

/**
 * Chat memory
 */
interface ChatMemory {
    /**
     * ID of the chat memory
     */
    fun id(): String

    /**
     * Add a message to the memory
     *
     * @param message Spring AI message to add
     */
    fun add(message: Message)

    /**
     * Return a list of messages in the memory
     *
     * @return A list of messages
     */
    fun messages(): List<Message>

    /**
     * Clear the memory, remove all messages
     */
    fun clear()
}