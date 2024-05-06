package io.github.llmagentbuilder.core.chatmemory

interface ChatMemoryProvider {
    fun provideChatMemory(
        chatMemoryStore: ChatMemoryStore,
        memoryId: String
    ): ChatMemory

    companion object {
        val DEFAULT = object : ChatMemoryProvider {
            override fun provideChatMemory(
                chatMemoryStore: ChatMemoryStore,
                memoryId: String
            ): ChatMemory {
                return MessageWindowChatMemory(chatMemoryStore, memoryId, 10)
            }
        }
    }
}