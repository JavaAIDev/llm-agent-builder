package com.javaaidev.llmagentbuilder.cli.command

import com.javaaidev.llmagentbuilder.cli.GenerationConfig
import com.javaaidev.llmagentbuilder.cli.MavenFilesGenerator
import org.apache.maven.cli.MavenCli
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

object CommandHelper {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val mavenCli = MavenCli()

    fun setupMavenProject(configFile: File): Path {
        val projectDir = Files.createTempDirectory("agent_app_")
        Files.createDirectories(projectDir)
        logger.info("Create project in directory : ${projectDir.toAbsolutePath()}")
        val config = com.javaaidev.llmagentbuilder.core.AgentConfigLoader.load(configFile)
        val pom = MavenFilesGenerator.generatePom(
            GenerationConfig(), config
        )
        writeFile(projectDir, "pom.xml", pom)
        return projectDir
    }

    private fun writeFile(dir: Path, filename: String, content: String) {
        Files.createDirectories(dir)
        Files.writeString(
            dir.resolve(filename),
            content
        )
    }

    fun runMavenCli(
        args: Array<String>,
        projectDir: Path,
    ): Int {
        val baseDir = projectDir.toAbsolutePath().toString()
        System.setProperty("maven.multiModuleProjectDirectory", baseDir)
        val commonArgs = arrayOf<String>()
        return mavenCli.doMain(
            commonArgs + args,
            baseDir,
            System.out,
            System.err
        )
    }

}