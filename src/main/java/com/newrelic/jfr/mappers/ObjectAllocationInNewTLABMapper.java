package com.newrelic.jfr.mappers;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Gauge;
import com.newrelic.telemetry.metrics.Metric;
import jdk.jfr.consumer.RecordedEvent;

import java.util.List;

public class ObjectAllocationInNewTLABMapper implements EventMapper {
    public static final String EVENT_NAME = "jdk.ObjectAllocationInNewTLAB";

    @Override
    public List<? extends Metric> apply(RecordedEvent ev) {
        var start = ev.getStartTime().toEpochMilli();
        var attr = new Attributes()
                .put("thread", ev.getThread("eventThread").getJavaName());

        return List.of(
                new Gauge("jfr:ObjectAllocationInNewTLAB.tlabSize", 0.0 + ev.getLong("tlabSize"), start, attr),
                new Gauge("jfr:ObjectAllocationInNewTLAB.allocation", 0.0 + ev.getLong("allocationSize"), start, attr)
        );
    }

    @Override
    public String getEventName() {
        return EVENT_NAME;
    }
}
