package io.opentelemetry.kotlin.config.yaml.tracing

import io.opentelemetry.kotlin.ExperimentalApi
import io.opentelemetry.kotlin.behavior.ConsoleExporterBehavior
import io.opentelemetry.kotlin.behavior.SpanProcessorBehavior
import io.opentelemetry.kotlin.config.schema.model.SpanProcessor

/**
 * Maps`tracer_provider.processors` onto processor behavior.
 */
@ExperimentalApi
fun List<SpanProcessor>.toBehavior(): SpanProcessorBehavior? {
    // TODO: Add support to multiple exporters being in use simultaneously once we have the SpanProcessor
    //  fully implemented.
    for (processor in this) {
        val simple = processor.simple?.toBehavior()
        val batch = processor.batch?.toBehavior()
        val consoleExporter = processor.simple?.exporter?.console ?: processor.batch?.exporter?.console
        if (consoleExporter != null) {
            return SpanProcessorBehavior(
                console = ConsoleExporterBehavior(),
                simple = simple,
                batch = batch,
            )
        }

        val httpExporter = processor.simple?.exporter?.otlpHttp ?: processor.batch?.exporter?.otlpHttp
        if (httpExporter != null) {
            return SpanProcessorBehavior(
                http = httpExporter.toBehavior(),
                simple = simple,
                batch = batch,
            )
        }
    }

    return null
}
