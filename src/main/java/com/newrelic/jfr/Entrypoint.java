package com.newrelic.jfr;

import com.newrelic.api.agent.Agent;
import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.telemetry.Attributes;

import java.lang.instrument.Instrumentation;
import java.util.logging.Level;

import static com.newrelic.jfr.Config.INSERT_API_KEY;
import static com.newrelic.jfr.Config.METRIC_INGEST_URI;
import static com.newrelic.jfr.attributes.AttributeNames.*;

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
            logger.log(Level.INFO, "New Relic JFR Monitor is disabled: JFR config has not been enabled in the Java agent.");
            return;
        }

        logger.log(Level.INFO, "Attaching New Relic JFR Monitor");

        try {
            String insertApiKey = agentConfig.getValue(INSERT_API_KEY);
            String metricIngestUri = agentConfig.getValue(METRIC_INGEST_URI);
            EventMapperRegistry registry = EventMapperRegistry.createDefault();

            boolean jfrAuditMode = agentConfig.getValue("jfr.audit_mode", false);
            var config = Config.builder()
                    .insertApiKey(insertApiKey)
                    .commonAttributes(COMMON_ATTRIBUTES)
                    .metricsIngestUri(metricIngestUri)
                    .registry(registry)
                    .auditMode(jfrAuditMode)
                    .logger(logger)
                    .build();

            var reporter = Reporter.build(config);
            reporter.start();
        } catch (Throwable t) {
            logger.log(Level.SEVERE, t, "Unable to attach New Relic JFR Monitor");
        }
    }

    static boolean isJfrDisabled(com.newrelic.api.agent.Config agentConfig) {
        return !agentConfig.getValue("jfr.enabled", false);
    }
}
