package io.github.alexcheng1982.llmagentbuilder.core.tool

import java.lang.reflect.Method
import java.util.function.Supplier

interface AgentToolFactory<out T : AgentTool<*, *>> {
    fun create(): T
}

interface ConfigurableAgentToolFactory<CONFIG, out T : ConfigurableAgentTool<*, *, CONFIG>> :
    AgentToolFactory<T> {
    fun create(config: CONFIG): T
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
        val setters = getPropertySetters()
        getEnvironmentVariables().forEach { (key, value) ->
            setters[key]?.run {
                invokeMethod(instance, this, value)
            }
        }
        return instance
    }

    private fun invokeMethod(instance: C, method: Method, value: String) {
        if (method.parameterCount != 1) {
            return
        }
        val parameter = method.parameters[0]
        val parameterValue = when (parameter.type) {
            Long::class.java -> value.toLongOrNull()
            Int::class.java -> value.toIntOrNull()
            Double::class.java -> value.toDoubleOrNull()
            Float::class.java -> value.toFloatOrNull()
            else -> value
        }
        method.invoke(instance, parameterValue)
    }

    private fun getPropertySetters(): Map<String, Method> {
        return configClass.methods.filter {
            it.name.startsWith("set")
        }.associateBy {
            it.name.removePrefix("set").lowercase()
        }
    }

    private fun getEnvironmentVariables(): Map<String, String> {
        val prefix = environmentVariablePrefix.lowercase()
        return System.getenv()
            .filterKeys {
                it.lowercase().startsWith(prefix)
            }.mapKeys { entry ->
                entry.key.lowercase()
                    .removePrefix(prefix)
            }
    }
}