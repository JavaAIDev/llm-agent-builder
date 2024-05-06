package io.github.alexcheng1982.llmagentbuilder.core.tool

import io.github.llmagentbuilder.core.tool.EnvironmentVariableConfigProvider
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.org.webcompere.systemstubs.SystemStubs.withEnvironmentVariable
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension
import kotlin.properties.Delegates
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(SystemStubsExtension::class)
class EnvironmentVariableConfigProviderTest {

    class TestConfig {
        lateinit var value: String
        var intValue by Delegates.notNull<Int>()
        var longValue by Delegates.notNull<Long>()
        var doubleValue by Delegates.notNull<Double>()
        var floatValue by Delegates.notNull<Float>()
    }

    class TestConfigProvider :
        EnvironmentVariableConfigProvider<TestConfig>(
            TestConfig::class.java,
            "testConfig_"
        )

    @Test
    fun testConfigProvider() {
        withEnvironmentVariable("testConfig_value", "hello")
            .and("testConfig_intValue", "100")
            .and("testConfig_longValue", "1000")
            .and("testConfig_doubleValue", "1.01")
            .and("testConfig_floatValue", "2.02")
            .execute {
                val config = TestConfigProvider().get()
                assertNotNull(config)
                assertEquals("hello", config.value)
                assertEquals(100, config.intValue)
                assertEquals(1000, config.longValue)
                assertEquals(1.01, config.doubleValue)
                assertEquals(2.02f, config.floatValue)
            }
    }
}