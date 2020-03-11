package com.newrelic.jfr.summarizers;

import com.newrelic.telemetry.metrics.Summary;
import jdk.jfr.consumer.RecordedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * This class handles all TLAB allocation JFR events, and delegates them to the actual
 * aggregators, which operate on a per-thread basis
 */
public final class ObjectAllocationOutsideTLABSummarizer implements EventSummarizer {

    public static final String EVENT_NAME = "jdk.ObjectAllocationOutsideTLAB";

    private final Map<String, EventSummarizer> perThread = new HashMap<>();

    @Override
    public String getEventName() {
        return EVENT_NAME;
    }

    @Override
    public void apply(RecordedEvent ev) {
        var threadName = Workarounds.getThreadName(ev);
        threadName.ifPresent(thread -> {
            if (perThread.get(thread) == null) {
                perThread.put(thread, new PerThreadObjectAllocationOutsideTLABSummarizer(thread));
            }
            perThread.get(thread).apply(ev);
        });
    }

    @Override
    public Stream<Summary> summarizeAndReset() {
        return perThread
                .values()
                .stream()
                .flatMap(EventSummarizer::summarizeAndReset);
    }
}
