package io.github.llmagentbuilder.plugin.observation.opentelemetry

import io.github.llmagentbuilder.core.AgentConfig
import io.github.llmagentbuilder.core.EvaluationHelper
import io.github.llmagentbuilder.core.ObservationPlugin
import io.micrometer.observation.ObservationRegistry
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

class OpenTelemetryPlugin : ObservationPlugin {
    override fun install(
        agentConfig: AgentConfig,
        observationRegistry: ObservationRegistry
    ) {
        val builder = Resource.builder()
        builder.put("service.name", agentConfig.metadata?.name)
        val exporterBuilder = OtlpHttpSpanExporter.builder()
        agentConfig.observation?.tracing?.exporter?.let {
            exporterBuilder.setEndpoint(it.endpoint)
            it.headers?.entries?.forEach { (k, v) ->
                exporterBuilder.addHeader(k, EvaluationHelper.evaluate(v))
            }
        }
        val resource = builder.build()
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

    private class OTelEventPublisher(private val listeners: List<EventListener>) :
        EventPublisher {
        override fun publishEvent(event: Any?) {
            listeners.forEach { it.onEvent(event) }
        }
    }
}