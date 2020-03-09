package com.newrelic.jfr.summarizers;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Summary;
import jdk.jfr.consumer.RecordedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * This class handles all TLAB allocation JFR events, and delegates them to the actual
 * aggregators, which operate on a per-thread basis
 */
public final class AllocationTLABSummarizer implements EventSummarizer {
    private static final int TOP_CLASSES_COUNT = 30;
    public static final String EVENT_NAME = "jdk.ObjectAllocationInNewTLAB";

    private final Map<String, EventSummarizer> perThread = new HashMap<>();
    private final Attributes atts;

    public AllocationTLABSummarizer(Attributes atts) {
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
            perThread.put(threadName, new PerThreadAllocationTLABSummarizer(threadName));
        }

        // apply to per-thread
        perThread.get(threadName).apply(ev);
    }

    @Override
    public List<Summary> summarizeAndReset() {
        var perThreadSummaries = perThread.values().stream().map(s -> s.summarizeAndReset()).flatMap(l -> l.stream()).collect(toList());
        var out = new ArrayList<Summary>();
        out.addAll(perThreadSummaries);
        return out;
    }
}
