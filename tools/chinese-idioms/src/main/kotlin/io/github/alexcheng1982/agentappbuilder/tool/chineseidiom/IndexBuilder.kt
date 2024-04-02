package io.github.alexcheng1982.agentappbuilder.tool.chineseidiom

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

data class Idiom(
    val derivation: String,
    val example: String,
    val explanation: String,
    val pinyin: String,
    val word: String,
    val abbreviation: String,
    val pinyin_r: String,
    val first: String,
    val last: String
)

object IndexBuilder {
    fun build(input: Path, output: Path) {
        val objectMapper =
            ObjectMapper().registerModules(KotlinModule.Builder().build())
        val idioms = objectMapper.readValue(
            input.toFile(),
            object : TypeReference<List<Idiom>>() {})
        idioms.map { it.word }
            .sorted()
            .joinToString("\n")
            .run {
                Files.writeString(
                    output.resolve("all-idioms.txt"),
                    this,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
                )
            }
    }
}

fun main() {
    IndexBuilder.build(
        Paths.get("D:\\idiom-database-master\\data\\idiom.json"),
        Paths.get(".")
    )
}