package com.newrelic.jfr.mappers;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Gauge;
import com.newrelic.telemetry.metrics.Metric;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedThread;

import java.util.List;

public class AllocationRequiringGCMapper implements EventMapper {
    public static final String EVENT_NAME = "jdk.AllocationRequiringGC";

    @Override
    public List<? extends Metric> apply(RecordedEvent ev) {
        var timestamp = ev.getStartTime().toEpochMilli();
        RecordedThread t = ev.getValue("eventThread");
        var attr = new Attributes()
                .put("thread.id", t.getId())
                .put("thread.name", t.getJavaName())
                .put("gcId", ev.getInt("gcId"));

        return List.of(
                new Gauge("jfr:AllocationRequiringGC.allocationSize", ev.getLong("size"), timestamp, attr)
        );
    }

    @Override
    public String getEventName() {
        return EVENT_NAME;
    }
}
