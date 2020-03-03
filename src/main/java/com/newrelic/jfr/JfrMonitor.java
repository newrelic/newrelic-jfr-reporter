package com.newrelic.jfr;

import com.newrelic.telemetry.metrics.MetricBuffer;
import jdk.jfr.consumer.RecordingStream;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class JfrMonitor {
    private final Supplier<RecordingStream> recordingStreamSupplier;
    private ExecutorService jfrMonitorService;
    private MapperRegistry mapperRegistry;

    public JfrMonitor(MapperRegistry registry) {
        this(RecordingStream::new, registry);
    }

    JfrMonitor(Supplier<RecordingStream> recordingStreamSupplier, MapperRegistry registry) {
        this.recordingStreamSupplier = recordingStreamSupplier;
        this.mapperRegistry = registry;
    }

    public void start() {
        jfrMonitorService = Executors.newSingleThreadExecutor();
        jfrMonitorService.submit(() -> {
            try (var recordingStream = recordingStreamSupplier.get()) {
                mapperRegistry.getMappers().forEach((name, consumer) -> {
                    recordingStream.enable(name).withPeriod(Duration.ofSeconds(1));
                    recordingStream.onEvent(name, consumer);
                });
                recordingStream.start(); //run forever
            }
        });
    }
}
