package com.newrelic.jfr;

import com.newrelic.jfr.mappers.EventMapper;
import com.newrelic.telemetry.metrics.MetricBuffer;
import jdk.jfr.consumer.RecordedEvent;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A very thin binding that applies an EventMapper to convert a JFR Event into
 * a list of New Relic dimensional Metrics and adds those to a MetricBuffer.
 */
public class JfrStreamEventMappingConsumer implements Consumer<RecordedEvent> {
    private final EventMapper mapper;
    private final Supplier<MetricBuffer> metricBufferSupplier;

    public JfrStreamEventMappingConsumer(EventMapper mapper, Supplier<MetricBuffer> metricBufferSupplier) {
        this.mapper = mapper;
        this.metricBufferSupplier = metricBufferSupplier;
    }

    @Override
    public void accept(RecordedEvent event) {
        // A single JFR event can produce several different dimensional metrics
        MetricBuffer metricBuffer = metricBufferSupplier.get();
        mapper.apply(event).forEach(metricBuffer::addMetric);
    }

    public String getEventName() {
        return mapper.getEventName();
    }
}