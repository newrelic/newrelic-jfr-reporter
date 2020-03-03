package com.newrelic.jfr;

import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.jfr.agent.AgentChangeListener;
import com.newrelic.jfr.agent.AgentPoller;
import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.SimpleMetricBatchSender;
import com.newrelic.telemetry.TelemetryClient;
import com.newrelic.telemetry.metrics.MetricBatch;
import com.newrelic.telemetry.metrics.MetricBatchSender;
import com.newrelic.telemetry.metrics.MetricBuffer;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

public class Reporter {
    private final Attributes commonAttributes;
    private final Logger logger;
    private final String insertApiKey;
    private final URI metricIngestUri;

    public static Reporter build(Config config) {
        return new Reporter(config.getCommonAttributes(), config.getLogger(), config.getInsertApiKey(),
                config.getMetricIngestUri());
    }

    Reporter(Attributes initialCommonAttributes, Logger logger, String insertApiKey, URI metricsIngestUri) {
        this.commonAttributes = new Attributes(initialCommonAttributes);
        this.logger = logger;
        this.insertApiKey = insertApiKey;
        this.metricIngestUri = metricsIngestUri;
    }

    public void start() throws MalformedURLException {
        var batchSendService = Executors.newSingleThreadScheduledExecutor();
        var metricBuffer = new MetricBuffer(commonAttributes);
        AtomicReference<MetricBuffer> metricBufferReference = new AtomicReference<>(metricBuffer);
        Consumer<MetricBuffer> sender = startTelemetrySdkReporter(batchSendService, metricBufferReference::get);
        var registry = new MapperRegistry(metricBufferReference::get);
        var jfrMonitor = new JfrMonitor(registry);

        logger.log(Level.INFO, "Starting New Relic JFR Monitor with ingest URI => " + metricIngestUri);

        jfrMonitor.start();

        var agentChangeListener = new AgentChangeListener(logger, commonAttributes, metricBufferReference, sender);
        AgentPoller.create(NewRelic.getAgent(), agentChangeListener).run();
    }

    private Consumer<MetricBuffer> startTelemetrySdkReporter(ScheduledExecutorService batchSendService,
                                                             Supplier<MetricBuffer> metricBufferSupplier)
            throws MalformedURLException {

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
