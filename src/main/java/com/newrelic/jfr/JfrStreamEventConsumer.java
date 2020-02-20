package com.newrelic.jfr;

import com.newrelic.telemetry.metrics.MetricBuffer;
import jdk.jfr.consumer.RecordedEvent;

import java.util.function.Consumer;

/**
 * A very thin binding that applies an EventMapper to convert a JFR Event into
 * a list of new relic dimensional Metrics and adds those to a MetricBuffer.
 */
public class JfrStreamEventConsumer implements Consumer<RecordedEvent> {

    private final EventMapper mapper;
    private final MetricBuffer metricBuffer;

    public JfrStreamEventConsumer(EventMapper mapper, MetricBuffer metricBuffer) {
        this.mapper = mapper;
        this.metricBuffer = metricBuffer;
    }

    @Override
    public void accept(RecordedEvent event) {
        // FIXME we can also filter at this point, e.g. to drop MONITOR_WAIT events from JFR threads themselves
        // A single JFR event can produce several different dimensional metrics
        mapper.apply(event)
            .forEach(metricBuffer::addMetric);
    }
}
