package io.github.llmagentbuilder.bootstrap

import org.slf4j.LoggerFactory
import java.nio.file.Path

object AgentMain {
    fun start(configFile: String?) {
        val logger = LoggerFactory.getLogger(javaClass)
        if (configFile != null) {
            logger.info("Bootstrap agent from config file $configFile")
            AgentBootstrap.bootstrap(Path.of(configFile))
        } else {
            javaClass.getResourceAsStream("agent.yaml")?.let {
                AgentBootstrap.bootstrap(it)
            } ?: {
                logger.error("No config file found")
            }
        }
    }
}

fun main(args: Array<String>) {
    AgentMain.start(args.firstOrNull())
}