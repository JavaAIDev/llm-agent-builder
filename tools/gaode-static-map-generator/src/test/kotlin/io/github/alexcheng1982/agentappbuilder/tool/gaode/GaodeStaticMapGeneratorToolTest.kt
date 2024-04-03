package io.github.alexcheng1982.agentappbuilder.tool.gaode

import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class GaodeStaticMapGeneratorToolTest {
    @Test
    fun testGenerateMarker() {
        val tool =
            GaodeStaticMapGeneratorToolFactory().create()
        val request =
            GaodeStaticMapGenerationRequest(
                listOf(
                    Location(
                        23.86,
                        121.62,
                        "Earthquake Location"
                    )
                )
            )
        val url = tool.apply(request).url
        assertNotNull(url)
        assertContains(url, "Earthquake")
        assertContains(url, "markers")
        assertFalse(url.contains("paths"))
        println(url)
    }

    @Test
    fun testGeneratePaths() {
        val tool =
            GaodeStaticMapGeneratorToolFactory().create(ToolConfig("demo"))
        val request =
            GaodeStaticMapGenerationRequest(
                listOf(
                    Location(0.0, 0.0, "T1"),
                    Location(1.0, 1.0, "T2")
                )
            )
        val url = tool.apply(request).url
        assertNotNull(url)
        assertContains(url, "paths")
        assertFalse(url.contains("markers"))
    }
}