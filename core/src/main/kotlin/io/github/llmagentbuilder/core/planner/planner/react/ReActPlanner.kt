package io.github.llmagentbuilder.core.planner.planner.react

import io.github.llmagentbuilder.core.planner.planner.LLMPlanner
import io.github.llmagentbuilder.core.planner.planner.LLMPlannerFactory
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.core.io.ClassPathResource

object ReActPlannerFactory : LLMPlannerFactory() {
    override fun defaultBuilder(): LLMPlanner.Builder {
        return LLMPlanner.Builder()
            .withUserPromptTemplate(PromptTemplate(ClassPathResource("prompts/react/user.st")))
            .withSystemPromptTemplate(PromptTemplate(ClassPathResource("prompts/react/system.st")))
            .withOutputParser(ReActOutputParser.INSTANCE)
            .withSystemInstruction("Answer the following questions as best you can.")
    }
}
