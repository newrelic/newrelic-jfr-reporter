package com.newrelic.jfr.mappers;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Gauge;
import com.newrelic.telemetry.metrics.Metric;
import jdk.jfr.consumer.RecordedEvent;

import java.util.List;

public class GarbageCollectionMapper implements EventMapper {

    public static final String EVENT_NAME = "jdk.GarbageCollection";

    @Override
    public List<? extends Metric> apply(RecordedEvent ev) {
        var timestamp = ev.getStartTime().toEpochMilli();
        double longestPause = ev.getDouble("longestPause");

        Attributes attr = new Attributes()
                .put("name", ev.getString("name"))
                .put("cause", ev.getString("cause"));

        return List.of(
                new Gauge("jfr:GarbageCollection.longestPause", longestPause, timestamp, attr)
        );
    }
}