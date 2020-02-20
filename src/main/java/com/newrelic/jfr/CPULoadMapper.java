package com.newrelic.jfr;

import com.newrelic.telemetry.metrics.Metric;
import jdk.jfr.consumer.RecordedEvent;

import java.util.List;

public class CPULoadMapper implements EventMapper {

    @Override
    public List<? extends Metric> apply(RecordedEvent recordedEvent) {
        return null;
    }
}
