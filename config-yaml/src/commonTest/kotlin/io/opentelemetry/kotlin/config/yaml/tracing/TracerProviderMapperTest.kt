package io.opentelemetry.kotlin.config.yaml.tracing

import io.opentelemetry.kotlin.behavior.IdGeneratorBehavior
import io.opentelemetry.kotlin.behavior.SamplerBehavior
import io.opentelemetry.kotlin.behavior.TracerProviderBehavior
import io.opentelemetry.kotlin.config.schema.model.AlwaysOffSampler
import io.opentelemetry.kotlin.config.schema.model.IdGenerator
import io.opentelemetry.kotlin.config.schema.model.RandomIdGenerator
import io.opentelemetry.kotlin.config.schema.model.Sampler
import io.opentelemetry.kotlin.config.schema.model.TracerProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class TracerProviderMapperTest {

    @Test
    fun unsetIfNoMappedFields() {
        assertNull(TracerProvider(processors = emptyList()).toBehavior())
    }

    @Test
    fun mapsTracerProviderFields() {
        val provider = TracerProvider(
            processors = emptyList(),
            sampler = Sampler(alwaysOff = AlwaysOffSampler()),
            idGenerator = IdGenerator(random = RandomIdGenerator()),
        )
        assertEquals(
            TracerProviderBehavior(
                sampler = SamplerBehavior.AlwaysOff,
                idGenerator = IdGeneratorBehavior.Random,
            ),
            provider.toBehavior(),
        )
    }
}
