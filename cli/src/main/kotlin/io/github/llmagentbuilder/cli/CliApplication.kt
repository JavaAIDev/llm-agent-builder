package io.github.llmagentbuilder.cli

import io.github.llmagentbuilder.cli.command.RunCommand
import picocli.CommandLine
import java.io.File
import java.util.concurrent.Callable
import kotlin.system.exitProcess

@CommandLine.Command(
    name = "llm-agent-builder",
    mixinStandardHelpOptions = true,
    version = ["0.2.0"],
    description = ["Build LLM agents"],
    subcommands = [RunCommand::class],
)
class CliApplication : Callable<Void> {
    @CommandLine.Option(
        names = ["-c", "--config"],
        description = ["agent config file"]
    )
    lateinit var configFile: File

    override fun call(): Void? {
        return null
    }
}

fun main(args: Array<String>): Unit =
    exitProcess(CommandLine(CliApplication()).execute(*args))