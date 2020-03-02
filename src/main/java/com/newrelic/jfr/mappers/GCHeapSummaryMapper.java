package com.newrelic.jfr.mappers;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Gauge;
import com.newrelic.telemetry.metrics.Metric;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedObject;

import java.util.List;

public class GCHeapSummaryMapper implements EventMapper {

    public static final String EVENT_NAME = "jdk.GCHeapSummary";

    @Override
    public List<? extends Metric> apply(RecordedEvent ev) {
        var timestamp = ev.getStartTime().toEpochMilli();
        RecordedObject heapSpace = ev.getValue("heapSpace");
        long memoryStart = heapSpace.getLong("start");
        long committedEnd = heapSpace.getLong("committedEnd");
        long committedSize = heapSpace.getLong("committedSize");
        long reservedEnd = heapSpace.getLong("reservedEnd");
        long reservedSize = heapSpace.getLong("reservedSize");

        Attributes attr = new Attributes()
                .put("gcId", ev.getInt("gcId"))
                .put("when", ev.getString("when"))
                .put("heapStart", memoryStart)
                .put("committedEnd", committedEnd)
                .put("reservedEnd", reservedEnd);

        return List.of(
                new Gauge("jfr:GCHeapSummary.heapUsed", ev.getLong("heapUsed"), timestamp, attr),
                new Gauge("jfr:GCHeapSummary.heapCommittedSize", committedSize, timestamp, attr),
                new Gauge("jfr:GCHeapSummary.reservedSize", reservedSize, timestamp, attr)
        );
    }
}