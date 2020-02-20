package com.newrelic.jfr;

import com.newrelic.telemetry.SimpleMetricBatchSender;
import com.newrelic.telemetry.TelemetryClient;
import com.newrelic.telemetry.metrics.MetricBatchSender;
import com.newrelic.telemetry.metrics.MetricBuffer;

import java.net.MalformedURLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class Reporter {

    private final Config config;

    public Reporter(Config config) {
        this.config = config;
    }

    public void start() throws MalformedURLException {
        var batchSendService = Executors.newSingleThreadScheduledExecutor();
        var metricBuffer = startTelemetrySdkReporter(batchSendService);
        var cpuMapper = new CPULoadMapper();
        var cpuEventConsumer = new JfrStreamEventConsumer(cpuMapper, metricBuffer);
        var jfrMonitor = new JfrMonitor(cpuEventConsumer);
        jfrMonitor.start();

    }

    private MetricBuffer startTelemetrySdkReporter(ScheduledExecutorService batchSendService) throws MalformedURLException {
        var metricBuffer = new MetricBuffer(config.getCommonAttributes());
        var apiKey = config.getApiKey();
        var metricsApiUrl = config.getMetricsApiUrl();
        MetricBatchSender metricBatchSender = SimpleMetricBatchSender.builder(apiKey)
                .uriOverride(metricsApiUrl)
                .enableAuditLogging()
                .build();

        var telemetryClient = new TelemetryClient(metricBatchSender, null);

        batchSendService.scheduleAtFixedRate(() -> {
                    config.getLogger().log(Level.FINE, "Sending JFR metrics batch to New Relic");
                    telemetryClient.sendBatch(metricBuffer.createBatch());
                }, 5, 5, TimeUnit.SECONDS
        );
        return metricBuffer;
    }
}
