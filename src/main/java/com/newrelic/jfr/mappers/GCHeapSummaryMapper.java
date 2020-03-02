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
        long heapUsed = ev.getLong("heapUsed");
        RecordedObject heapSpace = ev.getValue("heapSpace");
        long committedSize = heapSpace.getLong("committedSize");
        long reservedSize = heapSpace.getLong("reservedSize");

        Attributes attr = new Attributes()
                .put("gcId", ev.getInt("gcId"))
                .put("when", ev.getString("when"))
                .put("heapStart", heapSpace.getLong("start"))
                .put("committedEnd", heapSpace.getLong("committedEnd"))
                .put("reservedEnd", heapSpace.getLong("reservedEnd"));

        return List.of(
                new Gauge("jfr:GCHeapSummary.heapUsed", heapUsed, timestamp, attr),
                new Gauge("jfr:GCHeapSummary.heapCommittedSize", committedSize, timestamp, attr),
                new Gauge("jfr:GCHeapSummary.reservedSize", reservedSize, timestamp, attr)
        );
    }
}