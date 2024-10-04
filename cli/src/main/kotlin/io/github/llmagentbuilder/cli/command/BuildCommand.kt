package io.github.llmagentbuilder.cli.command

import io.github.llmagentbuilder.cli.CliApplication
import org.slf4j.LoggerFactory
import picocli.CommandLine
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.concurrent.Callable

enum class BuildOutputType { jar }

@CommandLine.Command(name = "build", description = ["Build an agent"])
class BuildCommand : Callable<Int> {
    @CommandLine.ParentCommand
    private val parent: CliApplication? = null

    @CommandLine.Option(
        names = ["-type", "--output-type"],
        description = ["Build output types: \${COMPLETION-CANDIDATES}"]
    )
    lateinit var outputType: BuildOutputType

    @CommandLine.Option(
        names = ["-o", "--output"],
        description = ["Output directory"]
    )
    var outputDir: File? = null

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun call(): Int? {
        val file = parent?.configFile ?: return null
        val projectDir = CommandHelper.setupMavenProject(file)
        val resourcesDir =
            projectDir.resolve("src").resolve("main").resolve("resources")
        Files.createDirectories(resourcesDir)
        Files.copy(
            file.toPath(),
            resourcesDir.resolve("agent.yaml")
        )
        val args = arrayOf("package")
        val result = CommandHelper.runMavenCli(args, projectDir)
        if (result == 0) {
            val outputDir = (outputDir?.toPath() ?: Path.of("."))
            Files.createDirectories(outputDir)
            val outputPath = outputDir.resolve("agent.jar")
            Files.copy(
                projectDir.resolve("target")
                    .resolve("agent-jar-with-dependencies.jar"),
                outputPath,
                StandardCopyOption.REPLACE_EXISTING
            )
            logger.info("Build jar file copied to $outputPath")
        }
        return result
    }
}