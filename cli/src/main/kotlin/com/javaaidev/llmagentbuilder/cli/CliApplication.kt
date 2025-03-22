package com.javaaidev.llmagentbuilder.cli

import com.javaaidev.llmagentbuilder.cli.command.BuildCommand
import com.javaaidev.llmagentbuilder.cli.command.RunCommand
import com.javaaidev.llmagentbuilder.core.VERSION
import picocli.CommandLine
import kotlin.system.exitProcess

@CommandLine.Command(
    name = "llm-agent-builder",
    mixinStandardHelpOptions = true,
    version = [VERSION],
    description = ["Build LLM agents"],
    subcommands = [RunCommand::class, BuildCommand::class],
)
class CliApplication

fun main(args: Array<String>): Unit =
    exitProcess(CommandLine(CliApplication()).execute(*args))