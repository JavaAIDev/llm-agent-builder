package io.github.llmagentbuilder.bootstrap

import java.nio.file.Paths

fun main(args: Array<String>) {
    val configPath =
        (args.firstOrNull()?.run { Paths.get(this) }) ?: throw RuntimeException(
            "No config file"
        )
    AgentBootstrap.bootstrap(configPath)
}