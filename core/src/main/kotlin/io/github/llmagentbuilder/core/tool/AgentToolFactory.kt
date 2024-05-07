package io.github.llmagentbuilder.core.tool

import org.apache.commons.beanutils.BeanUtils
import java.util.function.Supplier

/**
 * Factory to create agent tools
 *
 * Agent tool factories are loaded using [java.util.ServiceLoader].
 */
interface AgentToolFactory<out T : AgentTool<*, *>> {
    /**
     * @param T Agent tool type
     * @return Agent tool
     */
    fun create(): T
}

/**
 * Factory to create agent tools with configuration objects
 */
interface ConfigurableAgentToolFactory<CONFIG, out T : ConfigurableAgentTool<*, *, CONFIG>> :
    AgentToolFactory<T> {
    /**
     * @param T Agent tool type
     * @param config Tool configuration object
     * @return Agent tool
     */
    fun create(config: CONFIG): T

    /**
     * Name of configuration key
     *
     * @return Config name
     */
    fun configName(): String
}

abstract class BaseConfigurableAgentToolFactory<out T : ConfigurableAgentTool<*, *, CONFIG>, CONFIG>(
    private val configProvider: Supplier<CONFIG>
) : ConfigurableAgentToolFactory<CONFIG, T> {
    override fun create(): T {
        return create(configProvider.get())
    }
}

abstract class EnvironmentVariableConfigurableAgentToolFactory<out T : ConfigurableAgentTool<*, *, CONFIG>, CONFIG>(
    configClass: Class<CONFIG>,
    environmentVariablePrefix: String
) :
    BaseConfigurableAgentToolFactory<T, CONFIG>(
        EnvironmentVariableConfigProvider(
            configClass,
            environmentVariablePrefix
        )
    )

open class EnvironmentVariableConfigProvider<C>(
    private val configClass: Class<C>,
    private val environmentVariablePrefix: String
) :
    Supplier<C> {
    override fun get(): C {
        val instance = configClass.getDeclaredConstructor().newInstance()
        BeanUtils.populate(instance, getEnvironmentVariables())
        return instance
    }

    private fun getEnvironmentVariables(): Map<String, String> {
        val prefix = environmentVariablePrefix
        return System.getenv()
            .filterKeys {
                it.startsWith(prefix)
            }.mapKeys { entry ->
                entry.key.removePrefix(prefix)
            }
    }
}