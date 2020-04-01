package com.newrelic.jfr;

import static com.newrelic.jfr.Config.INSERT_API_KEY;
import static com.newrelic.jfr.Config.JFR_AUDIT_MODE;
import static com.newrelic.jfr.Config.JFR_ENABLED;
import static com.newrelic.jfr.Config.METRIC_INGEST_URI;
import static com.newrelic.jfr.attributes.AttributeNames.COLLECTOR_NAME;
import static com.newrelic.jfr.attributes.AttributeNames.INSTRUMENTATION_NAME;
import static com.newrelic.jfr.attributes.AttributeNames.INSTRUMENTATION_PROVIDER;

import com.newrelic.api.agent.Agent;
import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.telemetry.Attributes;
import java.lang.instrument.Instrumentation;
import java.util.logging.Level;

public class Entrypoint {

  private static final Attributes COMMON_ATTRIBUTES = new Attributes()
      .put(INSTRUMENTATION_NAME, "JFR")
      .put(INSTRUMENTATION_PROVIDER, "JFR Agent Extension")
      .put(COLLECTOR_NAME, "JFR Agent Extension");

  public static void premain(String agentArgs, Instrumentation inst) {
    Agent agent = NewRelic.getAgent();
    Logger logger = agent.getLogger();
    var agentConfig = agent.getConfig();

    if (isJfrDisabled(agentConfig)) {
      logger.log(Level.INFO,
          "New Relic JFR Monitor is disabled: JFR config has not been enabled in the Java agent.");
      return;
    }

    logger.log(Level.INFO, "Attaching New Relic JFR Monitor");

    try {
      String insertApiKey = agentConfig.getValue(INSERT_API_KEY);
      String metricIngestUri = agentConfig.getValue(METRIC_INGEST_URI);
      EventMapperRegistry eventMapperRegistry = EventMapperRegistry.createDefault();
      EventSummarizerRegistry eventSummarizerRegistry = EventSummarizerRegistry.createDefault();

      boolean jfrAuditMode = agentConfig.getValue(JFR_AUDIT_MODE, false);
      var config = Config.builder()
          .insertApiKey(insertApiKey)
          .commonAttributes(COMMON_ATTRIBUTES)
          .metricsIngestUri(metricIngestUri)
          .mapperRegistry(eventMapperRegistry)
          .summarizerRegistry(eventSummarizerRegistry)
          .auditMode(jfrAuditMode)
          .logger(logger)
          .build();

      var reporter = Reporter.build(config);
      reporter.start();
    } catch (Throwable t) {
      NewRelic.incrementCounter(MetricNames.SUPPORTABILITY_JFR_START_FAILED);
      logger.log(Level.SEVERE, t, "Unable to attach New Relic JFR Monitor");
    }
  }

  static boolean isJfrDisabled(com.newrelic.api.agent.Config agentConfig) {
    return !agentConfig.getValue(JFR_ENABLED, false);
  }
}
