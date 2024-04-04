package io.github.alexcheng1982.agentappbuilder.spring

import io.github.alexcheng1982.agentappbuilder.core.tool.AgentTool
import io.github.alexcheng1982.agentappbuilder.core.tool.AgentToolsProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

class SpringAgentToolsProvider : AgentToolsProvider, ApplicationContextAware {
    private lateinit var applicationContext: ApplicationContext

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val agents: Map<String, AgentTool<*, *>> by lazy {
        applicationContext.getBeansOfType(AgentTool::class.java)
            .values.associateBy { it.name() }
            .also {
                logger.info("Found agent tools {}", it.keys)
            }
    }

    override fun get(): Map<String, AgentTool<*, *>> {
        return agents
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}