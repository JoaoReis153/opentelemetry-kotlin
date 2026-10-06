package io.opentelemetry.kotlin.config.yaml

import io.opentelemetry.kotlin.ExperimentalApi
import io.opentelemetry.kotlin.behavior.ConsoleExporterBehavior
import io.opentelemetry.kotlin.behavior.LogRecordProcessorBehavior
import io.opentelemetry.kotlin.behavior.OtlpHttpExporterBehavior
import io.opentelemetry.kotlin.behavior.SimpleLogRecordProcessorBehavior
import io.opentelemetry.kotlin.config.schema.model.LogRecordProcessor
import io.opentelemetry.kotlin.config.schema.model.OtlpHttpExporter

/**
 * Maps`logger_provider.processors` onto processor behavior.
 */
@ExperimentalApi
fun List<LogRecordProcessor>.toBehavior(): LogRecordProcessorBehavior? {
    // TODO: Add support to multiple exporters being in use simultaneously once we have the LogRecordProcessor
    //  fully implemented.
    for (processor in this) {
        val simpleConsole = processor.simple?.exporter?.console?.let { ConsoleExporterBehavior() }
        if (simpleConsole != null) {
            return LogRecordProcessorBehavior(simple = SimpleLogRecordProcessorBehavior(console = simpleConsole))
        }
        val batchConsole = processor.batch?.exporter?.console?.let { ConsoleExporterBehavior() }
        if (batchConsole != null) {
            return LogRecordProcessorBehavior(console = batchConsole)
        }

        val simpleHttp = processor.simple?.exporter?.otlpHttp?.toBehavior()
        if (simpleHttp != null) {
            return LogRecordProcessorBehavior(simple = SimpleLogRecordProcessorBehavior(http = simpleHttp))
        }
        val batchHttp = processor.batch?.exporter?.otlpHttp?.toBehavior()
        if (batchHttp != null) {
            return LogRecordProcessorBehavior(http = batchHttp)
        }
    }

    return null
}

private fun OtlpHttpExporter.toBehavior(): OtlpHttpExporterBehavior {
    // if there are duplicate keys, the last one wins.
    // The spec says that in the case of duplicate keys, [headers] have a higher precedence.
    val mergedHeaders =
        OtlpHttpExporterBehavior.buildHeaderMap(headersList).orEmpty() +
            OtlpHttpExporterBehavior.buildHeaderMap(
                headers?.joinToString(separator = ",") { pair ->
                    "${pair.name}=${pair.value}"
                }
            ).orEmpty()
    return OtlpHttpExporterBehavior(
        endpoint = endpoint,
        timeout = timeout,
        headers = mergedHeaders.ifEmpty { null }
    )
}
