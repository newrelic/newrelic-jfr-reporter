package com.newrelic.jfr.mappers;

import com.newrelic.telemetry.metrics.Metric;
import jdk.jfr.consumer.RecordedEvent;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Convenience/Tag interface for defining how JFR events should turn into metrics.
 */
public interface EventMapper extends Function<RecordedEvent, List<? extends Metric>> {
    String getEventName();

    default Optional<Duration> getPollingDuration() {
        return Optional.empty();
    }
}
