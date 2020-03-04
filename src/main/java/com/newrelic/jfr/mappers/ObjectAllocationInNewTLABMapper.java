package com.newrelic.jfr.mappers;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Count;
import com.newrelic.telemetry.metrics.Metric;
import jdk.jfr.consumer.RecordedEvent;

import java.util.List;

public class ObjectAllocationInNewTLABMapper implements EventMapper {
    public static final String EVENT_NAME = "jdk.ObjectAllocationInNewTLAB";

    @Override
    public List<? extends Metric> apply(RecordedEvent ev) {
        var start = ev.getStartTime().toEpochMilli();
        var end = ev.getEndTime().toEpochMilli();
        var attr = new Attributes()
                .put("thread", ev.getThread("eventThread").getJavaName())
                .put("class", ev.getClass("objectClass").getName())
                .put("tlabSize", ev.getLong("tlabSize"));

        return List.of(
                new Count("jfr:ObjectAllocationInNewTLAB.allocation", 0.0 + ev.getLong("allocationSize"), start, end, attr)
        );
    }
}
