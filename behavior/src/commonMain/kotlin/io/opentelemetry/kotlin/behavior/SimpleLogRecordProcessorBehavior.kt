package io.opentelemetry.kotlin.behavior

import io.opentelemetry.kotlin.ExperimentalApi

/**
 * Unbuffered processing for the logger provider's processor: each log record is exported as it is
 * emitted. The exporter is configured on [LogRecordProcessorBehavior]. This type has no fields;
 * selecting it is the whole configuration.
 *
 * https://opentelemetry.io/docs/specs/otel/logs/sdk/#simple-processor
 */
@ExperimentalApi
data class SimpleLogRecordProcessorBehavior(
    /** Console log exporter. */
    val console: ConsoleExporterBehavior? = null,
    /** HTTP log exporter. */
    val http: OtlpHttpExporterBehavior? = null,
) : Behavior<SimpleLogRecordProcessorBehavior> {

    override fun mergeWith(higher: SimpleLogRecordProcessorBehavior): SimpleLogRecordProcessorBehavior = copy(
        console = mergeNode(console, higher.console),
        http = mergeNode(http, higher.http),
    )
}
