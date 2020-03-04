package com.newrelic.jfr.mappers;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Gauge;
import com.newrelic.telemetry.metrics.Metric;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedThread;

import java.util.List;

public class ThreadAllocationStatisticsMapper implements EventMapper {
    public static final String EVENT_NAME = "jdk.ThreadAllocationStatistics";

    @Override
    public List<? extends Metric> apply(RecordedEvent ev) {
        var time = ev.getStartTime().toEpochMilli();
        var allocated = ev.getDouble("allocated");
        RecordedThread t = ev.getValue("thread");
        var attr = new Attributes()
                .put("thread.name", t.getJavaName())
                .put("thread.osName", t.getOSName());

        return List.of(
                new Gauge("jfr:ThreadAllocationStatistics.allocated", allocated, time, attr)
        );
    }

    @Override
    public String getEventName() {
        return EVENT_NAME;
    }
}
