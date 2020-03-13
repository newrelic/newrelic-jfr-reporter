package com.newrelic.jfr.summarizers;

import com.newrelic.jfr.Workarounds;
import com.newrelic.telemetry.metrics.Summary;
import jdk.jfr.consumer.RecordedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * This class handles all TLAB allocation JFR events, and delegates them to the actual
 * aggregators, which operate on a per-thread basis
 */
public final class ObjectAllocationInNewTLABSummarizer implements EventSummarizer {

    public static final String EVENT_NAME = "jdk.ObjectAllocationInNewTLAB";

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
                perThread.put(thread,
                        new PerThreadObjectAllocationInNewTLABSummarizer(thread, ev.getStartTime().toEpochMilli()));
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