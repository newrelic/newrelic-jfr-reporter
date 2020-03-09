package com.newrelic.jfr.summarizers;

import com.newrelic.telemetry.metrics.Summary;
import jdk.jfr.consumer.RecordedEvent;

import java.util.List;

public interface EventSummarizer {
    String getEventName();

    void apply(RecordedEvent ev);

    List<Summary> summarizeAndReset();

}
