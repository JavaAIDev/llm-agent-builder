package com.javaaidev.llmagentbuilder.core

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

enum class MessageRole {
    user,
    assistant,
    system
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "role")
@JsonSubTypes(
    JsonSubTypes.Type(value = ThreadUserMessage::class, name = "user"),
    JsonSubTypes.Type(value = ThreadAssistantMessage::class, name = "assistant")
)
interface ThreadMessage {
    fun getRole(): MessageRole
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(JsonSubTypes.Type(value = TextContentPart::class, name = "text"))
interface ThreadUserContentPart {
    fun getType(): String
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(JsonSubTypes.Type(value = TextContentPart::class, name = "text"))
interface ThreadAssistantContentPart {
    fun getType(): String
}

data class ThreadUserMessage(var content: List<ThreadUserContentPart>) : ThreadMessage {
    override fun getRole(): MessageRole {
        return MessageRole.user
    }
}

data class ThreadAssistantMessage(var content: List<ThreadAssistantContentPart>) : ThreadMessage {
    override fun getRole(): MessageRole {
        return MessageRole.assistant
    }
}

data class TextContentPart(var text: String) : ThreadUserContentPart, ThreadAssistantContentPart {
    override fun getType(): String {
        return "text"
    }
}

data class ChatAgentRequest(var messages: List<ThreadMessage>)

data class ChatAgentResponse(var content: List<ThreadAssistantContentPart>) {
    companion object {
        fun fromMap(data: Map<String, Any>): ChatAgentResponse {
            return ChatAgentResponse(listOf(TextContentPart((data["output"] ?: "").toString())))
        }
    }
}