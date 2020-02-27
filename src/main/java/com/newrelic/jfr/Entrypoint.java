package com.newrelic.jfr;

import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.jfr.attributes.LinkingMetadata;
import com.newrelic.telemetry.Attributes;

import java.lang.instrument.Instrumentation;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import static com.newrelic.jfr.attributes.LinkingMetadata.*;

public class Entrypoint {
    public static void premain(String agentArgs, Instrumentation inst) {
        Logger logger = NewRelic.getAgent().getLogger();
        logger.log(Level.INFO, "Attaching New Relic JFR Monitor");
        String appName = NewRelic.getAgent().getConfig().getValue("app_name");

        // This needs to be on a different thread or else it blocks the agent initialization
        Executors.newSingleThreadScheduledExecutor().submit(() -> {
            try {
                waitForAgentInitialization(logger);

                var commonAttributes = new Attributes()
                        .put("host", getHostname())
                        .put(HOSTNAME, getHostname())
                        .put(ENTITY_GUID, getEntityGuid())
                        .put(ENTITY_NAME, getEntityName())
                        .put(ENTITY_TYPE, getEntityType())
                        .put(SPAN_ID, getSpanId())
                        .put(TRACE_ID, getTraceId())
                        .put("appName", appName)
                        .put("service.name", appName)
                        .put("instrumentation.name", "JFR")
                        .put("instrumentation.provider", "JFR Agent Extension")
                        .put("collector.name", "JFR Agent Extension");

                var agentConfig = NewRelic.getAgent().getConfig();
                String insertApiKey = agentConfig.getValue("insert_api_key");
                String metricIngestUri = agentConfig.getValue("metric_ingest_uri");

                var builder = Config.builder()
                        .insertApiKey(insertApiKey)
                        .commonAttributes(commonAttributes)
                        .logger(logger);

                if ((metricIngestUri != null) && (!metricIngestUri.isEmpty())) {
                    builder = builder.metricsIngestUri(URI.create(metricIngestUri));
                }
                var config = builder.build();
                new Reporter(config).start();
            } catch (Throwable t) {
                logger.log(Level.SEVERE, t, "Unable to attach New Relic JFR Monitor");
            }
        });
    }
}
