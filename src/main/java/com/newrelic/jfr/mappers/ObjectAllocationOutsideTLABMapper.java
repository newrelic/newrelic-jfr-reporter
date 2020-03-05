package com.newrelic.jfr.mappers;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Gauge;
import com.newrelic.telemetry.metrics.Metric;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedThread;

import java.util.List;

public class ObjectAllocationOutsideTLABMapper implements EventMapper {
    public static final String EVENT_NAME = "jdk.ObjectAllocationOutsideTLAB";

    @Override
    public List<? extends Metric> apply(RecordedEvent ev) {
        var start = ev.getStartTime().toEpochMilli();
        RecordedThread t = ev.getValue("eventThread");
        var attr = new Attributes().put("thread.name", t.getJavaName());

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
