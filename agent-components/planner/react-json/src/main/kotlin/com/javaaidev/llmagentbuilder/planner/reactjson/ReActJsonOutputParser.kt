package com.javaaidev.llmagentbuilder.planner.reactjson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.javaaidev.llmagentbuilder.core.AgentAction
import com.javaaidev.llmagentbuilder.core.AgentFinish
import com.javaaidev.llmagentbuilder.core.planner.JsonParser
import com.javaaidev.llmagentbuilder.core.planner.OutputParser
import com.javaaidev.llmagentbuilder.core.planner.ParseResult

class ReActJsonOutputParser : OutputParser {
    private val finalAnswerAction = "Final Answer:"

    private val objectMapper =
        ObjectMapper().registerModule(KotlinModule.Builder().build())

    override fun parse(text: String): ParseResult {
        val includeAnswer = text.contains(finalAnswerAction)
        if (includeAnswer) {
            val output = text.split(finalAnswerAction).last().trim()
            return ParseResult.finishWithOutputAndLog(output, text)
        }
        val response = JsonParser.parse(text)
        if (response != null) {
            val actionInput = response["action_input"]?.let {
                when (it) {
                    is String, Int, Long, Float, Double -> it.toString()
                    else -> objectMapper.writeValueAsString(it)
                }
            } ?: "{}"
            if (response["action"] == "Final Answer") {
                return ParseResult.finish(
                    AgentFinish(
                        mapOf(
                            "output" to actionInput
                        ), text
                    )
                )
            } else if (response["action"] != null) {
                return response["action"].let {
                    ParseResult.action(
                        AgentAction(
                            it.toString(),
                            actionInput,
                            text
                        )
                    )
                }
            }
        }
        return ParseResult.finishWithText(text)
    }

    companion object {
        val INSTANCE = ReActJsonOutputParser()
    }
}