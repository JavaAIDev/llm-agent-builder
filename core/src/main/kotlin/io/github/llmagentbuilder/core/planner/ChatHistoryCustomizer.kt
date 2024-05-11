package io.github.llmagentbuilder.core.planner

import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.UserMessage
import java.util.function.BiFunction

/**
 * Customize chat history before sending to LLM
 */
interface ChatHistoryCustomizer {
    fun customize(messages: List<Message>): List<Message>

    companion object {
        val DEFAULT = object : ChatHistoryCustomizer {
            override fun customize(messages: List<Message>): List<Message> {
                return messages
            }
        }
    }
}

/**
 * Patch content of last [UserMessage] in chat history
 */
open class PatchLastUserMessageChatHistoryCustomizer(
    private val messageContentCustomizer: BiFunction<List<Message>, String, String>
) :
    ChatHistoryCustomizer {
    override fun customize(messages: List<Message>): List<Message> {
        val lastUserMessage = messages.lastOrNull {
            it is UserMessage
        }
        lastUserMessage?.let {
            val index = messages.lastIndexOf(it)
            val updatedMessages = messages.toMutableList()
            updatedMessages[index] = UserMessage(
                messageContentCustomizer.apply(messages, it.content)
            )
            return updatedMessages
        }
        return messages
    }

}