package io.opentelemetry.kotlin.config.yaml.tracing

import io.opentelemetry.kotlin.behavior.BatchSpanProcessorBehavior
import io.opentelemetry.kotlin.config.schema.model.BatchSpanProcessor
import io.opentelemetry.kotlin.config.schema.model.SpanExporter
import kotlin.test.Test
import kotlin.test.assertEquals

internal class BatchSpanProcessorMapperTest {

    @Test
    fun everyFieldStartsWithDefaultValue() {
        val behavior = BatchSpanProcessor(exporter = SpanExporter()).toBehavior()
        assertEquals(
            BatchSpanProcessorBehavior(
                scheduleDelay = BatchSpanProcessorBehavior.DEFAULT_SCHEDULE_DELAY,
                exportTimeout = BatchSpanProcessorBehavior.DEFAULT_EXPORT_TIMEOUT,
                maxQueueSize = BatchSpanProcessorBehavior.DEFAULT_MAX_QUEUE_SIZE,
                maxExportBatchSize = BatchSpanProcessorBehavior.DEFAULT_MAX_EXPORT_BATCH_SIZE
            ),
            behavior
        )
    }

    @Test
    fun mapsFields() {
        val behavior = BatchSpanProcessor(
            scheduleDelay = 100,
            exportTimeout = 200,
            maxQueueSize = 400,
            maxExportBatchSize = 300,
            exporter = SpanExporter()
        ).toBehavior()
        assertEquals(
            BatchSpanProcessorBehavior(
                scheduleDelay = 100,
                exportTimeout = 200,
                maxQueueSize = 400,
                maxExportBatchSize = 300
            ),
            behavior
        )
    }

    @Test
    fun invalidFieldValuesFallbackToDefault() {
        val behavior = BatchSpanProcessor(
            scheduleDelay = -1,
            exportTimeout = -1,
            maxQueueSize = 0,
            maxExportBatchSize = 0,
            exporter = SpanExporter()
        ).toBehavior()
        assertEquals(
            BatchSpanProcessorBehavior(
                scheduleDelay = BatchSpanProcessorBehavior.DEFAULT_SCHEDULE_DELAY,
                exportTimeout = BatchSpanProcessorBehavior.DEFAULT_EXPORT_TIMEOUT,
                maxQueueSize = BatchSpanProcessorBehavior.DEFAULT_MAX_QUEUE_SIZE,
                maxExportBatchSize = BatchSpanProcessorBehavior.DEFAULT_MAX_EXPORT_BATCH_SIZE
            ),
            behavior
        )
    }

    @Test
    fun batchSizeIsCappedAtQueueSize() {
        val behavior = BatchSpanProcessor(
            maxQueueSize = 100,
            maxExportBatchSize = 400,
            exporter = SpanExporter()
        ).toBehavior()
        assertEquals(100, behavior.maxQueueSize)
        assertEquals(100, behavior.maxExportBatchSize)
    }

    @Test
    fun defaultBatchSizeIsCappedAtQueueSize() {
        val behavior = BatchSpanProcessor(maxQueueSize = 100, exporter = SpanExporter()).toBehavior()
        assertEquals(100, behavior.maxExportBatchSize)
    }
}
