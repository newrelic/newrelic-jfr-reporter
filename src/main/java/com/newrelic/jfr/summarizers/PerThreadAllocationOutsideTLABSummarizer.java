package com.newrelic.jfr.summarizers;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Summary;
import jdk.jfr.consumer.RecordedEvent;

import java.time.Instant;
import java.util.List;

/**
 * This class aggregates all TLAB allocation JFR events for a single thread
 */
public final class PerThreadAllocationOutsideTLABSummarizer implements EventSummarizer {

    private final String threadName;
    private int count = 0;
    private long sum = 0L;
    private long min = Long.MAX_VALUE;
    private long max = 0L;
    private long startTimeMs;
    private long endTimeMs = 0L;


    public PerThreadAllocationOutsideTLABSummarizer(String threadName) {
        this.threadName = threadName;
        this.startTimeMs = Instant.now().toEpochMilli();
    }

    @Override
    public String getEventName() {
        return AllocationOutsideTLABSummarizer.EVENT_NAME;
    }

    @Override
    public void apply(RecordedEvent ev) {
        endTimeMs = ev.getStartTime().toEpochMilli();
        count++;
        var alloc = ev.getLong("allocationSize");
        sum = sum + alloc;

        if (alloc > max) {
            max = alloc;
        }
        if (alloc < min) {
            min = alloc;
        }
        // Probably too high a cardinality
        // ev.getClass("objectClass").getName();
    }

    @Override
    public List<Summary> summarizeAndReset() {
        var attr = new Attributes();
        attr.put("threadName", threadName);
        var out = new Summary("jfr:ObjectAllocationOutsideTLAB.allocation", count, sum, min, max, startTimeMs, endTimeMs, attr);
        reset();
        return List.of(out);
    }

    public void reset() {
        startTimeMs = Instant.now().toEpochMilli();
        endTimeMs = 0L;
        count = 0;
        sum = 0L;
        min = Long.MAX_VALUE;
        max = 0L;
    }

}
