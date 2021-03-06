/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.newrelic.jfr;

import com.newrelic.api.agent.Agent;
import com.newrelic.api.agent.Logger;
import com.newrelic.jfr.agent.AgentAttributesChangeListener;
import com.newrelic.jfr.agent.AgentPoller;
import com.newrelic.telemetry.*;
import com.newrelic.telemetry.events.EventBatchSender;
import com.newrelic.telemetry.http.HttpPoster;
import com.newrelic.telemetry.metrics.MetricBatch;
import com.newrelic.telemetry.metrics.MetricBatchSender;
import com.newrelic.telemetry.metrics.MetricBuffer;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
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
  private final URI eventIngestUri;
  private final ToMetricRegistry toMetricRegistry;
  private final ToSummaryRegistry summarizerRegistry;
  private final boolean auditMode;
  private final Agent agent;

  Reporter(Config config, Agent agent) {
    this.commonAttributes = config.getCommonAttributes();
    this.logger = config.getLogger();
    this.insertApiKey = config.getInsertApiKey();
    this.metricIngestUri = config.getMetricIngestUri();
    this.eventIngestUri = config.getEventIngestUri();
    this.toMetricRegistry = config.getToMetricRegistry();
    this.summarizerRegistry = config.getToSummaryRegistry();
    this.auditMode = config.isAuditMode();
    this.agent = agent;
  }

  public void start() throws MalformedURLException {
    var batchSendService = Executors.newSingleThreadScheduledExecutor();
    var metricBuffer = new MetricBuffer(commonAttributes);
    var metricBufferReference = new AtomicReference<>(metricBuffer);
    var sender = startTelemetrySdkReporter(batchSendService, metricBufferReference::get);
    var jfrMonitor = new JfrMonitor(toMetricRegistry, summarizerRegistry, metricBufferReference::get);

    logger.log(Level.INFO, "Starting New Relic JFR Monitor with ingest URI => " + metricIngestUri);
    agent.getMetricAggregator()
        .incrementCounter(MetricNames.SUPPORTABILITY_JFR_START_OK);
    jfrMonitor.start();

    var agentChangeListener = new AgentAttributesChangeListener(logger, commonAttributes,
        metricBufferReference, sender);
    AgentPoller.create(agent, agentChangeListener).run();
  }

  private Consumer<MetricBuffer> startTelemetrySdkReporter(
      ScheduledExecutorService batchSendService,
      Supplier<MetricBuffer> metricBufferSupplier)
      throws MalformedURLException {

    Supplier<HttpPoster> httpPosterCreator =
            () -> new OkHttpPoster(Duration.of(10, ChronoUnit.SECONDS));

    var metricBatchSender =
            MetricBatchSender.create(
                    MetricBatchSenderFactory.fromHttpImplementation(httpPosterCreator)
                            .configureWith(insertApiKey)
                            .endpoint(metricIngestUri.toURL())
                            .auditLoggingEnabled(auditMode)
                            .build());

    var eventBatchSender =
            EventBatchSender.create(
                    EventBatchSenderFactory.fromHttpImplementation(httpPosterCreator)
                            .configureWith(insertApiKey)
                            .endpoint(eventIngestUri.toURL())
                            .auditLoggingEnabled(auditMode)
                            .build());

    var telemetryClient = new TelemetryClient(metricBatchSender, null, eventBatchSender, null);

    Consumer<MetricBuffer> send = metricBuffer -> {
      MetricBatch batch = metricBuffer.createBatch();
      logger.log(Level.FINE,
          "Sending JFR metrics batch size " + batch.getTelemetry().size() + " to New Relic");
      telemetryClient.sendBatch(batch);
    };

    batchSendService.scheduleAtFixedRate(() -> {
          MetricBuffer metricBuffer = metricBufferSupplier.get();
          send.accept(metricBuffer);
        }, 5, 5, TimeUnit.SECONDS
    );
    return send;
  }

  public static Reporter build(Config config, Agent agent) {
    return new Reporter(config, agent);
  }

}
