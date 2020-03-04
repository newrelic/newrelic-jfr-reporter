package com.newrelic.jfr.mappers;

import com.newrelic.telemetry.metrics.Metric;
import jdk.jfr.consumer.RecordedEvent;

import java.util.List;
import java.util.function.Function;

/**
 * Convenience/Tag interface for defining how JFR events should turn into metrics.
 */
public interface EventMapper extends Function<RecordedEvent, List<? extends Metric>> {
    String getEventName();
}
