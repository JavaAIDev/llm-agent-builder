package io.github.llmagentbuilder.cli.command

import io.github.llmagentbuilder.cli.CliApplication
import org.slf4j.LoggerFactory
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "run", description = ["Run an agent"])
class RunCommand : Callable<Int> {
    @CommandLine.ParentCommand
    private val parent: CliApplication? = null

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun call(): Int? {
        val file = parent?.configFile ?: return null
        val projectDir = CommandHelper.setupMavenProject(file)
        val configFileArg =
            file.toPath().normalize().toAbsolutePath().toString()
        val args = arrayOf(
            "exec:java",
            "-Dexec.args=\"${configFileArg}\""
        )
        return CommandHelper.runMavenCli(
            args, projectDir
        )
    }
}