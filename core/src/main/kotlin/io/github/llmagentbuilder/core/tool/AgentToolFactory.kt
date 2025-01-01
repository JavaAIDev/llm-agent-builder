package io.github.llmagentbuilder.core.tool

import java.util.function.Supplier

/**
 * Factory to create agent tools
 *
 * Agent tool factories are loaded using [java.util.ServiceLoader].
 */
interface AgentToolFactory<out T : AgentTool<*, *>> {
    /**
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
     * @param config Tool configuration object
     * @return Agent tool
     */
    fun create(config: CONFIG?): T

    /**
     * ID of the created tool
     *
     * @return Tool id
     */
    fun toolId(): String

    override fun create(): T {
        return create(null)
    }
}

abstract class BaseConfigurableAgentToolFactory<out T : ConfigurableAgentTool<*, *, CONFIG>, CONFIG>(
    private val configProvider: Supplier<CONFIG?>
) : ConfigurableAgentToolFactory<CONFIG, T> {
    override fun create(): T {
        return create(configProvider.get())
    }
}
