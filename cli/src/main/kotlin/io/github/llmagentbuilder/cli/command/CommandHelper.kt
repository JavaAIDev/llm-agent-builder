package io.github.llmagentbuilder.cli.command

import io.github.llmagentbuilder.cli.GenerationConfig
import io.github.llmagentbuilder.cli.MavenPomGenerator
import io.github.llmagentbuilder.core.AgentConfigLoader
import org.apache.commons.lang3.SystemUtils
import org.buildobjects.process.ProcBuilder
import org.slf4j.LoggerFactory
import org.zeroturnaround.zip.ZipUtil
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Consumer

object CommandHelper {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun setupMavenProject(configFile: File): Path {
        val projectDir = Files.createTempDirectory("agent_app_")
        Files.createDirectories(projectDir)
        logger.info("Create project in directory : ${projectDir.toAbsolutePath()}")
        RunCommand::class.java.getResourceAsStream("/maven.zip")?.use {
            ZipUtil.unpack(it, projectDir.toFile())
        }
        val config = AgentConfigLoader.load(configFile)
        val pom = MavenPomGenerator.generate(
            GenerationConfig(), config
        )
        Files.writeString(
            projectDir.resolve("pom.xml"),
            pom
        )
        return projectDir
    }

    fun mavenCommandBuilder(projectDir: Path): ProcBuilder {
        val command = if (SystemUtils.IS_OS_WINDOWS) "run-maven.bat" else "mvnw"
        return ProcBuilder(
            projectDir.resolve(command).normalize().toAbsolutePath().toString()
        )
            .withWorkingDirectory(projectDir.toFile())
            .withOutputStream(System.out)
            .withNoTimeout()
    }

    fun runMavenCommand(
        projectDir: Path,
        pbCustomizer: Consumer<ProcBuilder>
    ): Int {
        val pb = mavenCommandBuilder(projectDir)
        pbCustomizer.accept(pb)
        logger.info("Run command : ${pb.commandLine}")
        return pb.run().exitValue
    }
}