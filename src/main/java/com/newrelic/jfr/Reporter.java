package com.newrelic.jfr;

import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.jfr.agent.AgentPoller;
import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.SimpleMetricBatchSender;
import com.newrelic.telemetry.TelemetryClient;
import com.newrelic.telemetry.metrics.MetricBatch;
import com.newrelic.telemetry.metrics.MetricBatchSender;
import com.newrelic.telemetry.metrics.MetricBuffer;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

public class Reporter {
    private final Config config;
    private final Attributes commonAttributes;
    private final Logger logger;
    private final String insertApiKey;

    public static Reporter build(Config config) {
        return new Reporter(config, config.getCommonAttributes(), config.getLogger(), config.getInsertApiKey());
    }

    Reporter(Config config, Attributes initialCommonAttributes, Logger logger, String insertApiKey) {
        this.config = config;
        this.commonAttributes = new Attributes(initialCommonAttributes);
        this.logger = logger;
        this.insertApiKey = insertApiKey;
    }

    public void start() throws MalformedURLException {
        var batchSendService = Executors.newSingleThreadScheduledExecutor();
        var metricBuffer = new MetricBuffer(commonAttributes);
        AtomicReference<MetricBuffer> metricBufferReference = new AtomicReference<>(metricBuffer);
        Consumer<MetricBuffer> sender = startTelemetrySdkReporter(batchSendService, metricBufferReference::get);
        var registry = new MapperRegistry(metricBufferReference::get);
        var jfrMonitor = new JfrMonitor(registry);

        logger.log(Level.INFO, "Starting New Relic JFR Monitor with ingest URI => " + config.getMetricIngestUri());

        jfrMonitor.start();

        Consumer<Map<String, String>> agentChangeListener = attrs -> {
            logger.log(Level.INFO, "Refreshing the MetricBuffer for the New Relic JFR Monitor");

            attrs.forEach(commonAttributes::put);

            var newBuffer = new MetricBuffer(commonAttributes);

            MetricBuffer oldBuffer = metricBufferReference.getAndSet(newBuffer);
            logger.log(Level.INFO, "New Relic JFR Monitor: Sending with remaining old batch...");
            sender.accept(oldBuffer);
        };
        AgentPoller.create(NewRelic.getAgent(), agentChangeListener).run();
    }

    private Consumer<MetricBuffer> startTelemetrySdkReporter(ScheduledExecutorService batchSendService,
                                                             Supplier<MetricBuffer> metricBufferSupplier)
            throws MalformedURLException {
        var metricIngestUri = config.getMetricIngestUri();

        MetricBatchSender metricBatchSender = SimpleMetricBatchSender.builder(insertApiKey)
                .uriOverride(metricIngestUri)
                .enableAuditLogging()
                .build();

        var telemetryClient = new TelemetryClient(metricBatchSender, null);

        Consumer<MetricBuffer> send = metricBuffer -> {
            MetricBatch batch = metricBuffer.createBatch();
            logger.log(Level.FINE, "Sending JFR metrics batch size " + batch.getTelemetry().size() + " to New Relic");
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
