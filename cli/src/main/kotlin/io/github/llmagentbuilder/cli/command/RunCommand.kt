package io.github.llmagentbuilder.cli.command

import org.slf4j.LoggerFactory
import picocli.CommandLine
import java.io.File
import java.util.concurrent.Callable

@CommandLine.Command(name = "run", description = ["Run an agent"])
class RunCommand : Callable<Int> {
    @CommandLine.Option(
        names = ["-c", "--config"],
        description = ["agent config file"],
        required = true,
    )
    lateinit var configFile: File

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun call(): Int {
        logger.info(
            "Run an agent from config file: {}",
            configFile.toPath().normalize().toAbsolutePath()
        )
        val projectDir = CommandHelper.setupMavenProject(configFile)
        val configFileArg =
            configFile.toPath().normalize().toAbsolutePath().toString()
        val args = arrayOf(
            "exec:java",
            "-Dexec.args=\"${configFileArg}\""
        )
        return CommandHelper.runMavenCli(
            args, projectDir
        )
    }
}