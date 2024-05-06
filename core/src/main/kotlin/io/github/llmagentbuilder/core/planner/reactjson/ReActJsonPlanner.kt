package io.github.llmagentbuilder.core.planner.reactjson

import io.github.llmagentbuilder.core.planner.LLMPlanner
import io.github.llmagentbuilder.core.planner.LLMPlannerFactory
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.core.io.ClassPathResource

object ReActJsonPlannerFactory : LLMPlannerFactory() {
    override fun defaultBuilder(): LLMPlanner.Builder {
        return LLMPlanner.Builder()
            .withUserPromptTemplate(PromptTemplate(ClassPathResource("prompts/react-json/user.st")))
            .withSystemPromptTemplate(PromptTemplate(ClassPathResource("prompts/react-json/system.st")))
            .withOutputParser(ReActJsonOutputParser.INSTANCE)
            .withSystemInstruction("Answer the following questions as best you can.")
            .withStopSequence(listOf("\\nObservation"))
    }
}