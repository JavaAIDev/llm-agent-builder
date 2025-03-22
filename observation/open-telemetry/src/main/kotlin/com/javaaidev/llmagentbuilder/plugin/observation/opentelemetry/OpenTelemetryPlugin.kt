package com.javaaidev.llmagentbuilder.plugin.observation.opentelemetry

import com.javaaidev.llmagentbuilder.core.*
import io.micrometer.core.instrument.Clock
import io.micrometer.observation.ObservationRegistry
import io.micrometer.registry.otlp.OtlpConfig
import io.micrometer.registry.otlp.OtlpMeterRegistry
import io.micrometer.tracing.handler.DefaultTracingObservationHandler
import io.micrometer.tracing.otel.bridge.EventListener
import io.micrometer.tracing.otel.bridge.OtelCurrentTraceContext
import io.micrometer.tracing.otel.bridge.OtelTracer
import io.micrometer.tracing.otel.bridge.OtelTracer.EventPublisher
import io.micrometer.tracing.otel.bridge.Slf4JEventListener
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor
import org.springframework.ai.chat.observation.ChatModelMeterObservationHandler

class OpenTelemetryPlugin : ObservationPlugin {
    override fun install(
        agentConfig: com.javaaidev.llmagentbuilder.core.AgentConfig,
        observationRegistry: ObservationRegistry,
    ) {
        val builder = Resource.builder()
        builder.put("service.name", agentConfig.metadata?.name)
        val resource = builder.build()
        agentConfig.observation?.tracing?.let {
            applyTracingConfig(it, observationRegistry, resource)
        }
        agentConfig.observation?.metrics?.let {
            applyMetricsConfig(it, observationRegistry)
        }
    }

    private fun applyTracingConfig(
        tracingConfig: com.javaaidev.llmagentbuilder.core.TracingConfig,
        observationRegistry: ObservationRegistry,
        resource: Resource,
    ) {
        if (tracingConfig.enabled != true) {
            return
        }
        val exporterBuilder = OtlpHttpSpanExporter.builder()
        tracingConfig.exporter?.let {
            exporterBuilder.setEndpoint(it.endpoint)
            it.headers?.entries?.forEach { (k, v) ->
                exporterBuilder.addHeader(
                    k,
                    com.javaaidev.llmagentbuilder.core.EvaluationHelper.evaluate(v)
                )
            }
        }

        val tracer = SdkTracerProvider.builder()
            .setResource(resource)
            .addSpanProcessor(
                BatchSpanProcessor.builder(
                    exporterBuilder.build()
                ).build()
            )
            .build()
            .get("llm-agent")
        val context = OtelCurrentTraceContext()
        val otelTracer = OtelTracer(
            tracer, context, OTelEventPublisher(
                listOf(
                    Slf4JEventListener()
                )
            )
        )
        val handler = DefaultTracingObservationHandler(otelTracer)
        observationRegistry.observationConfig().observationHandler(handler)
    }

    private fun applyMetricsConfig(
        metricsConfig: com.javaaidev.llmagentbuilder.core.MetricsConfig,
        observationRegistry: ObservationRegistry,
    ) {
        if (metricsConfig.enabled != true) {
            return
        }
        metricsConfig.exporter?.let {

            val otlpConfig = object : OtlpConfig {
                override fun get(key: String): String? {
                    return null
                }

                override fun url(): String {
                    return it.endpoint
                }

                override fun headers(): Map<String, String> {
                    return it.headers?.mapValues { (_, v) ->
                        com.javaaidev.llmagentbuilder.core.EvaluationHelper.evaluate(v)
                    } ?: mapOf()
                }
            }
            val meterRegistry = OtlpMeterRegistry(otlpConfig, Clock.SYSTEM)
            val handler = ChatModelMeterObservationHandler(meterRegistry)
            observationRegistry.observationConfig().observationHandler(handler)
        }

    }

    private class OTelEventPublisher(private val listeners: List<EventListener>) :
        EventPublisher {
        override fun publishEvent(event: Any?) {
            listeners.forEach { it.onEvent(event) }
        }
    }
}