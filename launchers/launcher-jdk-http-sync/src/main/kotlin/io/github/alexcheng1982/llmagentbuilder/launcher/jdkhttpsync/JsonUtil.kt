package io.github.alexcheng1982.llmagentbuilder.launcher.jdkhttpsync

import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

internal object JsonUtil {
    fun <T> fromJson(
        json: String,
        classType: Class<T>,
        objectMapper: ObjectMapper
    ): T {
        return objectMapper.readValue(json, classType)
    }

    fun toJson(input: Any, objectMapper: ObjectMapper): String {
        return try {
            objectMapper.writeValueAsString(input)
        } catch (e: Exception) {
            Objects.toString(input)
        }
    }
}