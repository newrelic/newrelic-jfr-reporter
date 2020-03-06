package com.newrelic.jfr.mappers;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Gauge;
import com.newrelic.telemetry.metrics.Metric;
import jdk.jfr.consumer.RecordedEvent;

import java.util.List;

public class ObjectAllocationOutsideTLABMapper implements EventMapper {
    public static final String EVENT_NAME = "jdk.ObjectAllocationOutsideTLAB";

    @Override
    public List<? extends Metric> apply(RecordedEvent ev) {
        var start = ev.getStartTime().toEpochMilli();
        var attr = new Attributes();
        var threadName = Workarounds.getThreadName(ev);
        threadName.ifPresent(t -> attr.put("thread.name", t));

        return List.of(
                new Gauge("jfr:ObjectAllocationOutsideTLAB.allocation", 0.0 + ev.getLong("allocationSize"),
                        start, attr)
        );
    }

    @Override
    public String getEventName() {
        return EVENT_NAME;
    }
}
