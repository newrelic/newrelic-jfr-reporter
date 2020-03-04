package com.newrelic.jfr.mappers;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Gauge;
import com.newrelic.telemetry.metrics.Metric;
import jdk.jfr.consumer.RecordedEvent;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static java.util.concurrent.TimeUnit.SECONDS;

public class CPULoadMapper implements EventMapper {

    public static final String EVENT_NAME = "jdk.CPULoad";

    @Override
    public List<? extends Metric> apply(RecordedEvent ev) {
        var timestamp = ev.getStartTime().toEpochMilli();
        var baseAttributes = new Attributes();
        return List.of(
                new Gauge("jfr:CPULoad.jvmUser", ev.getDouble("jvmUser"), timestamp, baseAttributes),
                new Gauge("jfr:CPULoad.jvmSystem", ev.getDouble("jvmSystem"), timestamp, baseAttributes),
                new Gauge("jfr:CPULoad.machineTotal", ev.getDouble("machineTotal"), timestamp, baseAttributes)
        );
    }

    @Override
    public String getEventName() {
        return EVENT_NAME;
    }

    @Override
    public Optional<Duration> getPollingDuration() {
        return Optional.of(Duration.of(1, SECONDS.toChronoUnit()));
    }
}
