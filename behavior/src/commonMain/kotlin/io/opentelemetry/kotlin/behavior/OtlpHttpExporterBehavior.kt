package io.opentelemetry.kotlin.behavior

import io.opentelemetry.kotlin.ExperimentalApi

@ExperimentalApi
/**
 * Selecting the OTLP HTTP exporter.
 *
 * https://opentelemetry.io/docs/specs/otel/protocol/exporter/
 */
data class OtlpHttpExporterBehavior(
    // TODO: Add all fields supported by the spec.
    //  Blocked by #974. (OTLP exporter configuration surface)
    /**
     * Target to which the exporter is going to send spans, metrics, or logs.
     */
    val endpoint: String? = null,
    /**
     * Maximum time (in milliseconds) to wait for each export.
     */
    val timeout: Long? = null,
    /**
     * Configure headers.
     */
    val headers: Map<String, String?>? = null,
) : Behavior<OtlpHttpExporterBehavior> {
    override fun mergeWith(higher: OtlpHttpExporterBehavior): OtlpHttpExporterBehavior {
        return copy(
            endpoint = higher.endpoint ?: endpoint,
            timeout = higher.timeout ?: timeout,
            headers = mergeMap(headers, higher.headers),
        )
    }

    companion object {
        fun buildHeaderMap(headerString: String?): Map<String, String>? {
            headerString ?: return null
            return buildMap {
                headerString.split(",").forEach { header ->
                    // Trailing and leading whitespaces are allowed but not considered part of key/value.
                    // See https://www.w3.org/TR/baggage/#key and https://www.w3.org/TR/baggage/#value
                    val (key, value) = header.split("=", limit = 2).map(String::trim)
                    if (key.isNotEmpty() && value.isNotEmpty()) {
                        put(key, value)
                    }
                }
            }
        }
    }
}
