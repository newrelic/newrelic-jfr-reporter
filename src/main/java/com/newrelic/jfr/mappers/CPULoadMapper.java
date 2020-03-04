package com.newrelic.jfr.mappers;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Gauge;
import com.newrelic.telemetry.metrics.Metric;
import jdk.jfr.consumer.RecordedEvent;

import java.util.List;

public class CPULoadMapper implements EventMapper {

    public static final String EVENT_NAME = "jdk.CPULoad";

    @Override
    public List<? extends Metric> apply(RecordedEvent ev) {
        var timestamp = ev.getStartTime().toEpochMilli();
        var attr = new Attributes();
        return List.of(
                new Gauge("jfr:CPULoad.jvmUser", ev.getDouble("jvmUser"), timestamp, attr),
                new Gauge("jfr:CPULoad.jvmSystem", ev.getDouble("jvmSystem"), timestamp, attr),
                new Gauge("jfr:CPULoad.machineTotal", ev.getDouble("machineTotal"), timestamp, attr)
        );
    }
}
