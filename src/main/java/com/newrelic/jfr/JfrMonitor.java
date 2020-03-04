package com.newrelic.jfr;

import com.newrelic.jfr.mappers.EventMapper;
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
    private ExecutorService jfrMonitorService;

    public JfrMonitor(EventMapperRegistry registry, Supplier<MetricBuffer> metricBufferSupplier) {
        this(registry, metricBufferSupplier, RecordingStream::new);
    }

    JfrMonitor(EventMapperRegistry registry, Supplier<MetricBuffer> metricBufferSupplier, Supplier<RecordingStream> recordingStreamSupplier) {
        this.recordingStreamSupplier = recordingStreamSupplier;
        this.eventMapperRegistry = registry;
        this.metricBufferSupplier = metricBufferSupplier;
    }

    public void start() {
        jfrMonitorService = Executors.newSingleThreadExecutor();
        jfrMonitorService.submit(() -> {
            try (var recordingStream = recordingStreamSupplier.get()) {
                var enableEvent = eventEnablerFor(recordingStream);
                eventMapperRegistry.getMappers().forEach(enableEvent);
                recordingStream.start(); //run forever
            }
        });
    }

    private Consumer<EventMapper> eventEnablerFor(RecordingStream recordingStream) {
        return mapper -> {
            EventSettings eventSettings = recordingStream.enable(mapper.getEventName());
            mapper.getPollingDuration().ifPresent(eventSettings::withPeriod);
            recordingStream.onEvent(mapper.getEventName(), new JfrStreamEventConsumer(mapper, metricBufferSupplier));
        };
    }
}
