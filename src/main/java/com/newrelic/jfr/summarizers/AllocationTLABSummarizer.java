package com.newrelic.jfr.summarizers;

import com.newrelic.telemetry.metrics.Summary;
import jdk.jfr.consumer.RecordedEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * This class handles all TLAB allocation JFR events, and delegates them to the actual
 * aggregators, which operate on a per-thread basis
 */
public final class AllocationTLABSummarizer implements EventSummarizer {

    public static final String EVENT_NAME = "jdk.ObjectAllocationInNewTLAB";

    private final Map<String, EventSummarizer> perThread = new HashMap<>();

    @Override
    public String getEventName() {
        return EVENT_NAME;
    }

    @Override
    public void apply(RecordedEvent ev) {
        // Lookup thread
        // TODO use WorkArounds.getThreadName(ev)
        var threadName = ev.getThread("eventThread").getJavaName();
        if (perThread.get(threadName) == null) {
            perThread.put(threadName, new PerThreadAllocationTLABSummarizer(threadName));
        }
        // apply to per-thread
        perThread.get(threadName).apply(ev);
    }

    @Override
    public List<Summary> summarizeAndReset() {
        return perThread
                .values()
                .stream()
                .map(EventSummarizer::summarizeAndReset)
                .flatMap(Collection::stream)
                .collect(toList());
    }
}
