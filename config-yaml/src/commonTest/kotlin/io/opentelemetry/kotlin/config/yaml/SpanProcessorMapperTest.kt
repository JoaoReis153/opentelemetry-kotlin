package io.opentelemetry.kotlin.config.yaml

import io.opentelemetry.kotlin.behavior.ConsoleExporterBehavior
import io.opentelemetry.kotlin.behavior.OtlpHttpExporterBehavior
import io.opentelemetry.kotlin.behavior.SpanProcessorBehavior
import io.opentelemetry.kotlin.config.schema.model.BatchSpanProcessor
import io.opentelemetry.kotlin.config.schema.model.ConsoleExporter
import io.opentelemetry.kotlin.config.schema.model.NameStringValuePair
import io.opentelemetry.kotlin.config.schema.model.OtlpHttpExporter
import io.opentelemetry.kotlin.config.schema.model.SimpleSpanProcessor
import io.opentelemetry.kotlin.config.schema.model.SpanExporter
import io.opentelemetry.kotlin.config.schema.model.SpanProcessor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class SpanProcessorMapperTest {

    @Test
    fun emptyProcessorsLeaveBehaviorUnset() {
        assertNull(emptyList<SpanProcessor>().toBehavior())
    }

    @Test
    fun mapsConsoleFromASimpleProcessor() {
        val processors = listOf(
            SpanProcessor(simple = SimpleSpanProcessor(exporter = consoleExporter())),
        )
        assertEquals(SpanProcessorBehavior(console = ConsoleExporterBehavior()), processors.toBehavior())
    }

    @Test
    fun mapsHttpFromASimpleProcessor() {
        val processors = listOf(
            SpanProcessor(simple = SimpleSpanProcessor(exporter = httpExporter())),
        )
        assertEquals(
            SpanProcessorBehavior(
                http = OtlpHttpExporterBehavior(
                    endpoint = "http://localhost:4317",
                    timeout = 10_000,
                    headers = mapOf("key" to "value")
                )
            ),
            processors.toBehavior(),
        )
    }

    @Test
    fun mapsConsoleFromABatchProcessor() {
        val processors = listOf(
            SpanProcessor(batch = BatchSpanProcessor(exporter = consoleExporter())),
        )
        assertEquals(SpanProcessorBehavior(console = ConsoleExporterBehavior()), processors.toBehavior())
    }

    @Test
    fun mapsHttpFromABatchProcessor() {
        val processors = listOf(SpanProcessor(batch = BatchSpanProcessor(exporter = httpExporter())))
        assertEquals(
            SpanProcessorBehavior(
                http = OtlpHttpExporterBehavior(
                    endpoint = "http://localhost:4317",
                    timeout = 10_000,
                    headers = mapOf("key" to "value")
                )
            ),
            processors.toBehavior(),
        )
    }

    @Test
    fun httpExporterHeaderHaveHigherPriorityThanHeaderList() {
        val processors = listOf(
            SpanProcessor(
                batch = BatchSpanProcessor(
                    exporter = SpanExporter(
                        otlpHttp = OtlpHttpExporter(
                            endpoint = "http://localhost:4317",
                            timeout = 10_000,
                            headersList = "key=value2",
                            headers = listOf(NameStringValuePair("key", "value"))
                        )
                    )
                )
            )
        )
        assertEquals(
            SpanProcessorBehavior(
                http = OtlpHttpExporterBehavior(
                    endpoint = "http://localhost:4317",
                    timeout = 10_000,
                    headers = mapOf("key" to "value")
                )
            ),
            processors.toBehavior(),
        )
    }

    @Test
    fun leavesProcessorsWithNoKnownExportersUnset() {
        val processors = listOf(
            SpanProcessor(simple = SimpleSpanProcessor(exporter = SpanExporter())),
        )
        assertNull(processors.toBehavior())
    }

    private fun consoleExporter() = SpanExporter(console = ConsoleExporter())
    private fun httpExporter() = SpanExporter(
        otlpHttp = OtlpHttpExporter(
            endpoint = "http://localhost:4317",
            timeout = 10_000,
            headersList = "key=value"
        )
    )
}
