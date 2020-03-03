package com.newrelic.jfr.mappers;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Gauge;
import com.newrelic.telemetry.metrics.Metric;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedObject;

import java.util.ArrayList;
import java.util.List;

public class MetaspaceSummaryMapper implements EventMapper {

    public static final String EVENT_NAME = "jdk.MetaspaceSummary";
    private final String nrMetricPrefix = "jfr:MetaspaceSummary.";
    private final String metaspaceKey = "metaspace";
    private final String dataSpaceKey = "dataSpace";
    private final String classSpaceKey = "classSpace";

    @Override
    public List<? extends Metric> apply(RecordedEvent ev) {
        var timestamp = ev.getStartTime().toEpochMilli();
        RecordedObject metaspace = ev.getValue(metaspaceKey);
        RecordedObject dataSpace = ev.getValue(dataSpaceKey);
        RecordedObject classSpace = ev.getValue(classSpaceKey);

        Attributes attr = new Attributes()
                .put("gcId", ev.getInt("gcId"))
                .put("when", ev.getString("when"));

        List<Metric> metrics = new ArrayList<>(9);
        metrics.addAll(generateMetric(metaspaceKey, metaspace, attr, timestamp));
        metrics.addAll(generateMetric(dataSpaceKey, dataSpace, attr, timestamp));
        metrics.addAll(generateMetric(classSpaceKey, classSpace, attr, timestamp));

        return metrics;
    }

    private List<? extends Metric> generateMetric(String name, RecordedObject recordedObject, Attributes attr, long timestamp) {
        return List.of(
                new Gauge(nrMetricPrefix + name + ".committed", recordedObject.getDouble("committed"), timestamp, attr),
                new Gauge(nrMetricPrefix + name + ".used", recordedObject.getDouble("used"), timestamp, attr),
                new Gauge(nrMetricPrefix + name + ".reserved", recordedObject.getDouble("reserved"), timestamp, attr)
        );

    }
}