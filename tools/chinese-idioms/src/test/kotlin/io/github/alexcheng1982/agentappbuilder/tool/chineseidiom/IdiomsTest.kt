package io.github.alexcheng1982.agentappbuilder.tool.chineseidiom

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class IdiomsTest {
    @Test
    fun testIdiomCheck() {
        assertTrue(Idioms.isIdiom("一马当先"))
        assertFalse(Idioms.isIdiom("这不是成语"))
    }

    @Test
    fun testIdiomsBeginWith() {
        val idioms = Idioms.idiomsBeginWith("一")
        assertEquals(10, idioms.size)
    }
}