package com.newrelic.jfr;

import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.jfr.agent.AgentAttributesChangeListener;
import com.newrelic.jfr.agent.AgentPoller;
import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.SimpleMetricBatchSender;
import com.newrelic.telemetry.TelemetryClient;
import com.newrelic.telemetry.metrics.MetricBatch;
import com.newrelic.telemetry.metrics.MetricBatchSender;
import com.newrelic.telemetry.metrics.MetricBatchSenderBuilder;
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
    private final EventMapperRegistry registry;
    private final boolean auditMode;

    Reporter(Config config) {
        this.commonAttributes = config.getCommonAttributes();
        this.logger = config.getLogger();
        this.insertApiKey = config.getInsertApiKey();
        this.metricIngestUri = config.getMetricIngestUri();
        this.registry = config.getRegistry();
        this.auditMode = config.isAuditMode();
    }

    public void start() throws MalformedURLException {
        var batchSendService = Executors.newSingleThreadScheduledExecutor();
        var metricBuffer = new MetricBuffer(commonAttributes);
        var metricBufferReference = new AtomicReference<>(metricBuffer);
        var sender = startTelemetrySdkReporter(batchSendService, metricBufferReference::get);
        var jfrMonitor = new JfrMonitor(registry, metricBufferReference::get);

        logger.log(Level.INFO, "Starting New Relic JFR Monitor with ingest URI => " + metricIngestUri);

        jfrMonitor.start();

        var agentChangeListener = new AgentAttributesChangeListener(logger, commonAttributes, metricBufferReference, sender);
        AgentPoller.create(NewRelic.getAgent(), agentChangeListener).run();
    }

    private Consumer<MetricBuffer> startTelemetrySdkReporter(ScheduledExecutorService batchSendService,
                                                             Supplier<MetricBuffer> metricBufferSupplier)
            throws MalformedURLException {

        MetricBatchSenderBuilder builder = SimpleMetricBatchSender.builder(insertApiKey)
            .uriOverride(metricIngestUri);
        if(auditMode){
            builder.enableAuditLogging();
        }
        MetricBatchSender metricBatchSender = builder.build();

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

    public static Reporter build(Config config) {
        return new Reporter(config);
    }

}
