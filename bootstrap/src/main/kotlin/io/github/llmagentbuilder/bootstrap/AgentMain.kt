package io.github.llmagentbuilder.bootstrap

import io.github.llmagentbuilder.core.VERSION
import org.slf4j.LoggerFactory
import java.nio.file.Path

object AgentMain {
    fun start(configFile: String? = null) {
        val logger = LoggerFactory.getLogger(javaClass)
        logger.info("Agent version: {}", VERSION)
        if (configFile != null) {
            logger.info("Bootstrap agent from config file $configFile")
            AgentBootstrap.bootstrap(Path.of(configFile))
        } else {
            javaClass.getResourceAsStream("/agent.yaml")?.let {
                AgentBootstrap.bootstrap(it)
            } ?: run {
                logger.error("No config file found")
            }
        }
    }
}

fun main(args: Array<String>) {
    AgentMain.start(args.firstOrNull())
}