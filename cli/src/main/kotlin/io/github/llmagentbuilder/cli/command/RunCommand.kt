package io.github.llmagentbuilder.cli.command

import io.github.llmagentbuilder.cli.CliApplication
import org.slf4j.LoggerFactory
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "run", description = ["Run an agent"])
class RunCommand : Callable<Int> {
    @CommandLine.ParentCommand
    private val parent: CliApplication? = null

    @CommandLine.Option(
        names = ["-d"],
        description = ["Enabled remote debugging"]
    )
    var debug: Boolean? = false

    @CommandLine.Option(
        names = ["--debug-port"],
        description = ["Remote debug port"],
        defaultValue = "5005"
    )
    var debugPort: Int = 5005

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun call(): Int? {
        val file = parent?.configFile ?: return null
        val projectDir = CommandHelper.setupMavenProject(file)
        val mavenOpts = mutableListOf<String>()
        if (debug == true) {
            mavenOpts.add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:$debugPort")
        }
        val configFileArg =
            file.toPath().normalize().toAbsolutePath().toString()
        return CommandHelper.runMavenCommand(projectDir) { pb ->
            pb.withArgs("exec:java")
                .withArgs("-Dexec.args=\"${configFileArg}\"")
                .withVar("MAVEN_OPTS", mavenOpts.joinToString(" "))
        }
    }
}