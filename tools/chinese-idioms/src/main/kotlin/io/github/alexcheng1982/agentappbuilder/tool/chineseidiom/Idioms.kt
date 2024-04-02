package io.github.alexcheng1982.agentappbuilder.tool.chineseidiom

import java.io.BufferedReader
import java.io.InputStreamReader

object Idioms {
    private val idioms: List<String> by lazy {
        Idioms.javaClass.getResourceAsStream("/all-idioms.txt")?.use {
            BufferedReader(InputStreamReader(it)).lines().toList()
        } ?: listOf()
    }

    private val firstWordIndex: Map<String, List<String>> by lazy {
        idioms.groupBy { it.substring(0, 1) }
    }

    fun isIdiom(word: String): Boolean {
        return idioms.binarySearch(word) >= 0
    }

    fun idiomsBeginWith(word: String, limit: Int = 10): List<String> {
        return firstWordIndex[word]?.shuffled()?.take(1.coerceAtLeast(limit))
            ?: listOf()
    }
}