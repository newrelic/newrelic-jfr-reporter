package com.newrelic.jfr;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.SimpleMetricBatchSender;
import com.newrelic.telemetry.TelemetryClient;
import com.newrelic.telemetry.metrics.MetricBatch;
import com.newrelic.telemetry.metrics.MetricBatchSender;
import com.newrelic.telemetry.metrics.MetricBuffer;

import java.net.MalformedURLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

public class Reporter {
    private final Config config;

    public Reporter(Config config) {
        this.config = config;
    }

    public void start() throws MalformedURLException {
        var batchSendService = Executors.newSingleThreadScheduledExecutor();
        var metricBuffer = new MetricBuffer(config.getCommonAttributes());
        AtomicReference<MetricBuffer> metricBufferReference = new AtomicReference<>(metricBuffer);
        Consumer<MetricBuffer> sender = startTelemetrySdkReporter(batchSendService, metricBufferReference::get);
        var cpuMapper = new CPULoadMapper();
        var cpuEventConsumer = new JfrStreamEventConsumer(cpuMapper, metricBufferReference::get);
        var jfrMonitor = new JfrMonitor(cpuEventConsumer);

        config.getLogger().log(Level.INFO, "Starting New Relic JFR Monitor with ingest URI => " + config.getMetricIngestUri());

        jfrMonitor.start();

        ScheduledExecutorService sched = Executors.newSingleThreadScheduledExecutor();
        sched.scheduleAtFixedRate(() -> {
            config.getLogger().log(Level.INFO, "Refreshing the MetricBuffer for the New Relic JFR Monitor");

            Attributes commonAttributes = new Attributes(config.getCommonAttributes());
            commonAttributes.put("setup.time", System.currentTimeMillis());

            var newBuffer = new MetricBuffer(commonAttributes);
            MetricBuffer oldBuffer = metricBufferReference.getAndSet(newBuffer);

            config.getLogger().log(Level.INFO, "New Relic JFR Monitor: Sending with remaining old batch...");
            sender.accept(oldBuffer);
        }, 10, 10, TimeUnit.SECONDS);
    }

    private Consumer<MetricBuffer> startTelemetrySdkReporter(ScheduledExecutorService batchSendService,
                                                             Supplier<MetricBuffer> metricBufferSupplier)
            throws MalformedURLException {
        var insertApiKey = config.getInsertApiKey();
        var metricIngestUri = config.getMetricIngestUri();

        MetricBatchSender metricBatchSender = SimpleMetricBatchSender.builder(insertApiKey)
                .uriOverride(metricIngestUri)
                .enableAuditLogging()
                .build();

        var telemetryClient = new TelemetryClient(metricBatchSender, null);

        Consumer<MetricBuffer> send = metricBuffer -> {
            MetricBatch batch = metricBuffer.createBatch();
            config.getLogger().log(Level.FINE, "Sending JFR metrics batch size " + batch.getTelemetry().size() + " to New Relic");
            config.getLogger().log(Level.FINE, "    mb -> " + metricBuffer.hashCode());
            telemetryClient.sendBatch(batch);
        };

        batchSendService.scheduleAtFixedRate(() -> {
                    MetricBuffer metricBuffer = metricBufferSupplier.get();
                    send.accept(metricBuffer);
                }, 5, 5, TimeUnit.SECONDS
        );
        return send;
    }
}
