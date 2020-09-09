/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.newrelic.jfr;

import com.newrelic.jfr.tometric.EventToMetric;
import com.newrelic.jfr.tosummary.EventToSummary;
import com.newrelic.telemetry.metrics.MetricBuffer;
import jdk.jfr.EventSettings;
import jdk.jfr.consumer.RecordingStream;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class JfrMonitor {
    private final Supplier<RecordingStream> recordingStreamSupplier;
    private final Supplier<MetricBuffer> metricBufferSupplier;
    private final ToMetricRegistry toMetricRegistry;
    private final ToSummaryRegistry toSummaryRegistry;

    public JfrMonitor(ToMetricRegistry mapperRegistry, ToSummaryRegistry summarizerRegistry, Supplier<MetricBuffer> metricBufferSupplier) {
        this(mapperRegistry, summarizerRegistry, metricBufferSupplier, () -> {
            RecordingStream stream = new RecordingStream();
            stream.setReuse(false);
            return stream;
        });
    }

    JfrMonitor(ToMetricRegistry mapperRegistry, ToSummaryRegistry summarizerRegistry, Supplier<MetricBuffer> metricBufferSupplier, Supplier<RecordingStream> recordingStreamSupplier) {
        this.recordingStreamSupplier = recordingStreamSupplier;
        this.toMetricRegistry = mapperRegistry;
        this.toSummaryRegistry = summarizerRegistry;
        this.metricBufferSupplier = metricBufferSupplier;
    }

    public void start() {
        ExecutorService jfrMonitorService = Executors.newSingleThreadExecutor();
        jfrMonitorService.submit(() -> {
            try (var recordingStream = recordingStreamSupplier.get()) {
                var enableMappedEvent = eventMapperEnablerFor(recordingStream);
                var enableSummarizedEvent = eventSummarizerEnablerFor(recordingStream);
                toMetricRegistry.all().forEach(enableMappedEvent);
                toSummaryRegistry.all().forEach(enableSummarizedEvent);
                recordingStream.start(); //run forever
            }
        });
    }

    private Consumer<EventToMetric> eventMapperEnablerFor(RecordingStream recordingStream) {
        return mapper -> {
            EventSettings eventSettings = recordingStream.enable(mapper.getEventName());
            mapper.getPollingDuration().ifPresent(eventSettings::withPeriod);
            recordingStream.onEvent(mapper.getEventName(), new JfrStreamEventMappingConsumer(mapper, metricBufferSupplier));
        };
    }

    private Consumer<EventToSummary> eventSummarizerEnablerFor(RecordingStream recordingStream) {
        return summarizer -> {
            recordingStream.enable(summarizer.getEventName());
            recordingStream.onEvent(summarizer.getEventName(), new JfrStreamEventSummarizingConsumer(summarizer, metricBufferSupplier));
        };
    }
}
