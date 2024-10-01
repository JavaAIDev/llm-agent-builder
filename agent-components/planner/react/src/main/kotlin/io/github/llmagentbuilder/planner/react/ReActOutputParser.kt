package io.github.llmagentbuilder.planner.react

import io.github.llmagentbuilder.core.AgentAction
import io.github.llmagentbuilder.core.planner.OutputParser
import io.github.llmagentbuilder.core.planner.OutputParserException
import io.github.llmagentbuilder.core.planner.ParseResult
import org.slf4j.LoggerFactory
import java.util.regex.Pattern

class ReActOutputParser : OutputParser {
    private val finalAnswerAction = "Final Answer:"
    private val missingActionAfterThoughtErrorMessage =
        "Invalid Format: Missing 'Action:' after 'Thought:"
    private val missingActionInputAfterActionErrorMessage =
        "Invalid Format: Missing 'Action Input:' after 'Action:'"
    private val finalAnswerAndParsableActionErrorMessage =
        "Parsing LLM output produced both a final answer and a parse-able action:"

    private val patternFlag = Pattern.DOTALL or Pattern.MULTILINE
    private val pattern =
        Pattern.compile(
            ".*Action\\s*\\d*\\s*:\\s*(.*?)\\s*Action\\s*\\d*\\s*Input\\s*\\d*\\s*:\\s*(.*)",
            patternFlag
        )
    private val actionPattern =
        Pattern.compile(".*Action\\s*\\d*\\s*:\\s*(.*?)", patternFlag)
    private val actionInputPattern = Pattern.compile(
        "\\s*Action\\s*\\d*\\s*Input\\s*\\d*\\s*:\\s*(.*)",
        patternFlag
    )

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun parse(text: String): ParseResult {
        val includeAnswer = text.contains(finalAnswerAction)
        val actionMatch = pattern.matcher(text)
        if (actionMatch.matches()) {
            if (includeAnswer) {
                logger.warn("$finalAnswerAndParsableActionErrorMessage $text")
                return ParseResult.finishWithOutputAndLog(
                    text.split(finalAnswerAction).last().trim(),
                    text
                )
            }
            val action = actionMatch.group(1).trim()
            val actionInput = actionMatch.group(2)
            val toolInput = actionInput.trim(' ').trim('"')
            return ParseResult.action(
                AgentAction(
                    action,
                    toolInput,
                    text
                )
            )
        } else if (includeAnswer) {
            return ParseResult.finishWithOutputAndLog(
                text.split(finalAnswerAction).last().trim(),
                text
            )
        } else if (!actionPattern.matcher(text).matches()) {
            throw OutputParserException(
                "Could not parse LLM output: $text",
                missingActionAfterThoughtErrorMessage,
                text,
                true,
            )
        } else if (!actionInputPattern.matcher(text).matches()) {
            throw OutputParserException(
                "Could not parse LLM output: $text",
                missingActionInputAfterActionErrorMessage,
                text,
                true,
            )
        }
        throw OutputParserException("Could not parse LLM output: $text")
    }

    companion object {
        val INSTANCE = ReActOutputParser()
    }
}