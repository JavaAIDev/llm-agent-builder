package io.github.llmagentbuilder.cli

import io.github.llmagentbuilder.cli.command.BuildCommand
import io.github.llmagentbuilder.cli.command.RunCommand
import picocli.CommandLine
import java.io.File
import kotlin.system.exitProcess

@CommandLine.Command(
    name = "llm-agent-builder",
    mixinStandardHelpOptions = true,
    version = ["0.3.0"],
    description = ["Build LLM agents"],
    subcommands = [RunCommand::class, BuildCommand::class],
)
class CliApplication {
    @CommandLine.Option(
        names = ["-c", "--config"],
        description = ["agent config file"],
        required = true,
    )
    lateinit var configFile: File
}

fun main(args: Array<String>): Unit =
    exitProcess(CommandLine(CliApplication()).execute(*args))