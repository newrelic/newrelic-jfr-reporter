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

    /**
     * JFR event name (e.g. jdk.ObjectAllocationInNewTLAB)
     * @return String representation of JFR event name
     */
    String getEventName();

    /**
     * Optionally returns a polling duration for JFR events, if present
     * @return Optional<Duration> polling duration
     */
    default Optional<Duration> getPollingDuration() {
        return Optional.empty();
    }
}
