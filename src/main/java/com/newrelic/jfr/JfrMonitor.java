package com.newrelic.jfr;

import jdk.jfr.consumer.RecordingStream;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class JfrMonitor {

    private final Supplier<RecordingStream> recordingStreamSupplier;
    private final JfrStreamEventConsumer cpuEventConsumer;
    private ExecutorService jfrMonitorService;

    public JfrMonitor(JfrStreamEventConsumer cpuEventConsumer) {
        this(cpuEventConsumer, RecordingStream::new);
    }

    public JfrMonitor(JfrStreamEventConsumer cpuEventConsumer, Supplier<RecordingStream> recordingStreamSupplier) {
        this.cpuEventConsumer = cpuEventConsumer;
        this.recordingStreamSupplier = recordingStreamSupplier;
    }

    public void start() {
        jfrMonitorService = Executors.newSingleThreadExecutor();
        jfrMonitorService.submit(() -> {
            try (var recordingStream = recordingStreamSupplier.get()) {
                recordingStream.enable("jdk.CPULoad").withPeriod(Duration.ofSeconds(1));;
                recordingStream.onEvent("jdk.CPULoad", cpuEventConsumer);
                recordingStream.start();    //run forever
            }
        });
    }
}
