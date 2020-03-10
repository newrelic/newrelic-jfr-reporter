package com.newrelic.jfr;

import com.newrelic.jfr.mappers.EventMapper;
import com.newrelic.jfr.summarizers.EventSummarizer;
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
    private final EventMapperRegistry eventMapperRegistry;
    private final EventSummarizerRegistry eventSummarizerRegistry;

    public JfrMonitor(EventMapperRegistry mapperRegistry, EventSummarizerRegistry summarizerRegistry, Supplier<MetricBuffer> metricBufferSupplier ) {
        this(mapperRegistry, summarizerRegistry, metricBufferSupplier, () -> {
            RecordingStream stream = new RecordingStream();
            stream.setReuse(false);
            return stream;
        });
    }

    JfrMonitor(EventMapperRegistry mapperRegistry, EventSummarizerRegistry summarizerRegistry, Supplier<MetricBuffer> metricBufferSupplier, Supplier<RecordingStream> recordingStreamSupplier) {
        this.recordingStreamSupplier = recordingStreamSupplier;
        this.eventMapperRegistry = mapperRegistry;
        this.eventSummarizerRegistry = summarizerRegistry;
        this.metricBufferSupplier = metricBufferSupplier;
    }

    public void start() {
        ExecutorService jfrMonitorService = Executors.newSingleThreadExecutor();
        jfrMonitorService.submit(() -> {
            try (var recordingStream = recordingStreamSupplier.get()) {
                var enableMappedEvent = eventMapperEnablerFor(recordingStream);
                var enableSummarizedEvent = eventSummarizerEnablerFor(recordingStream);
                eventMapperRegistry.stream().forEach(enableMappedEvent);
                eventSummarizerRegistry.stream().forEach(enableSummarizedEvent);
                recordingStream.start(); //run forever
            }
        });
    }

    private Consumer<EventMapper> eventMapperEnablerFor(RecordingStream recordingStream) {
        return mapper -> {
            EventSettings eventSettings = recordingStream.enable(mapper.getEventName());
            mapper.getPollingDuration().ifPresent(eventSettings::withPeriod);
            recordingStream.onEvent(mapper.getEventName(), new JfrStreamEventMappingConsumer(mapper, metricBufferSupplier));
        };
    }
    private Consumer<EventSummarizer> eventSummarizerEnablerFor(RecordingStream recordingStream) {
        return summarizer -> {
            recordingStream.enable(summarizer.getEventName());
            recordingStream.onEvent(summarizer.getEventName(), new JfrStreamEventSummarizingConsumer(summarizer, metricBufferSupplier));
        };
    }
}
