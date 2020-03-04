package com.newrelic.jfr;

import com.newrelic.jfr.mappers.EventMapper;
import com.newrelic.telemetry.metrics.MetricBuffer;
import jdk.jfr.consumer.RecordingStream;

import java.time.Duration;
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
                var registerMapper = registerMapper(recordingStream);
                eventMapperRegistry.getMappers().forEach(registerMapper);
                recordingStream.start(); //run forever
            }
        });
    }

    private Consumer<EventMapper> registerMapper(RecordingStream recordingStream) {
        return mapper -> {
            //TODO: Get duration from the mapper
            recordingStream.enable(mapper.getEventName()).withPeriod(Duration.ofSeconds(1));
            recordingStream.onEvent(mapper.getEventName(), new JfrStreamEventConsumer(mapper, metricBufferSupplier));
        };
    }
}
