/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.newrelic.jfr;

import com.newrelic.jfr.tosummary.EventToSummary;
import com.newrelic.telemetry.metrics.MetricBuffer;
import jdk.jfr.consumer.RecordedEvent;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A very thin binding that applies an EventSummarizer to aggregate a JFR Event into
 * a list of New Relic Summary dimensional metrics. At the end of the summary period
 * it adds those to a MetricBuffer.
 */
public final class JfrStreamEventSummarizingConsumer implements Consumer<RecordedEvent> {

    private static final int SUMMARY_PERIOD = 60;
    private final EventToSummary summarizer;
    private final Supplier<MetricBuffer> metricBufferSupplier;
    private LocalDateTime lastSent;

    public JfrStreamEventSummarizingConsumer(EventToSummary summarizer, Supplier<MetricBuffer> metricBufferSupplier) {
        this.summarizer = summarizer;
        this.metricBufferSupplier = metricBufferSupplier;
        this.lastSent = LocalDateTime.now();
    }

    @Override
    public void accept(RecordedEvent event) {
        summarizer.accept(event);
        var metricBuffer = metricBufferSupplier.get();
        if (LocalDateTime.now().isAfter(lastSent.plusSeconds(SUMMARY_PERIOD))) {
            summarizer.summarize().forEach(metricBuffer::addMetric);
            lastSent = LocalDateTime.now();
        }
    }
}
