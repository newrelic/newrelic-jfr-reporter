package com.newrelic.jfr.summarizers;

import com.newrelic.telemetry.metrics.Summary;
import jdk.jfr.consumer.RecordedEvent;

import java.util.List;

public interface EventSummarizer {

    /**
     * JFR event name (e.g. jdk.ObjectAllocationInNewTLAB)
     * @return String representation of JFR event name
     */
    String getEventName();

    /**
     * Aggregates JFR Events into a collection based on thread or class name
     * @param ev JFR RecordedEvent
     */
    void apply(RecordedEvent ev);

    /**
     * Summarizes data on a collection of JFR Events
     * @return List of Summary metrics for JFR Events
     */
    List<Summary> summarizeAndReset();
}
