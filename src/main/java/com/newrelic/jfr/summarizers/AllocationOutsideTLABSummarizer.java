package com.newrelic.jfr.summarizers;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Summary;
import jdk.jfr.consumer.RecordedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * This class handles all TLAB allocation JFR events, and delegates them to the actual
 * aggregators, which operate on a per-thread basis
 */
public final class AllocationOutsideTLABSummarizer implements EventSummarizer {

    private final Map<String, EventSummarizer> perThread = new HashMap<>();

    public static final String EVENT_NAME = "jdk.ObjectOutsideTLAB";

    Attributes atts;

    public AllocationOutsideTLABSummarizer(Attributes atts) {
        this.atts = atts;
    }

    @Override
    public String getEventName() {
        return EVENT_NAME;
    }

    @Override
    public void apply(RecordedEvent ev) {
        // Lookup thread
        var threadName = ev.getThread("eventThread").getJavaName();
        if (perThread.get(threadName) == null) {
            perThread.put(threadName, new PerThreadAllocationOutsideTLABSummarizer(threadName));
        }

        // apply to per-thread
        perThread.get(threadName).apply(ev);
        // As it stands we never prune dead threads - we may need to consider the possibility at some point.
    }

    @Override
    public List<Summary> summarizeAndReset() {
        return perThread.values().stream().map(s -> s.summarizeAndReset()).flatMap(l -> l.stream()).collect(toList());
    }
}
