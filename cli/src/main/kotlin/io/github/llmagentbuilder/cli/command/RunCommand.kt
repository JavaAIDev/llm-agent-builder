package io.github.llmagentbuilder.cli.command

import io.github.llmagentbuilder.cli.CliApplication
import io.github.llmagentbuilder.cli.GenerationConfig
import io.github.llmagentbuilder.cli.MavenPomGenerator
import io.github.llmagentbuilder.core.AgentConfigLoader
import org.apache.commons.lang3.SystemUtils
import org.buildobjects.process.ProcBuilder
import org.slf4j.LoggerFactory
import org.zeroturnaround.zip.ZipUtil
import picocli.CommandLine
import java.nio.file.Files
import java.util.concurrent.Callable

@CommandLine.Command(name = "run", description = ["Run an agent"])
class RunCommand : Callable<Void> {
    @CommandLine.ParentCommand
    private val parent: CliApplication? = null

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun call(): Void? {
        val file = parent?.configFile ?: return null
        val config = AgentConfigLoader.load(file)
        val pom = MavenPomGenerator.generate(
            GenerationConfig(), config
        )
        val projectDir = Files.createTempDirectory("agent_app_")
//        val projectDir = Path.of(".", "test-app1")
        Files.createDirectories(projectDir)
        logger.info("Create project in directory : ${projectDir.toAbsolutePath()}")
        RunCommand::class.java.getResourceAsStream("/maven.zip")?.use {
            ZipUtil.unpack(it, projectDir.toFile())
        }
        Files.writeString(
            projectDir.resolve("pom.xml"),
            pom
        )
        val debugArg =
            "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5155"
        val configFileArg =
            file.toPath().normalize().toAbsolutePath().toString()
        val execArgs = listOf(debugArg, configFileArg).joinToString(" ")
        val command = if (SystemUtils.IS_OS_WINDOWS) "run-maven.bat" else "mvnw"
        val pb = ProcBuilder(
            projectDir.resolve(command).normalize().toAbsolutePath().toString()
        )
            .withWorkingDirectory(projectDir.toFile())
            .withArgs("exec:java")
            .withArgs("-Dexec.args=\"${configFileArg}\"")
            .withOutputStream(System.out)
            .withVar("MAVEN_OPTS", debugArg)
            .withNoTimeout()
        logger.info("Run command : ${pb.commandLine}")
        pb.run()
        return null
    }
}