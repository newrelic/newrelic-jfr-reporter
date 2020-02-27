package com.newrelic.jfr;

import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.jfr.attributes.CommonAttributes;
import com.newrelic.jfr.attributes.LinkingMetadata;

import java.lang.instrument.Instrumentation;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import static com.newrelic.jfr.Config.INSERT_API_KEY;
import static com.newrelic.jfr.Config.METRIC_INGEST_URI;

public class Entrypoint {
    public static void premain(String agentArgs, Instrumentation inst) {
        Logger logger = NewRelic.getAgent().getLogger();
        logger.log(Level.INFO, "Attaching New Relic JFR Monitor");

        // This needs to be on a different thread or else it blocks agent initialization
        Executors.newSingleThreadScheduledExecutor().submit(() -> {
            try {
                // Busy wait until RPMService has started and agent linking metadata is available
                LinkingMetadata.waitForAgentInitialization(logger);

                var agentConfig = NewRelic.getAgent().getConfig();
                String insertApiKey = agentConfig.getValue(INSERT_API_KEY);

                var builder = Config.builder()
                        .insertApiKey(insertApiKey)
                        .commonAttributes(new CommonAttributes().get())
                        .logger(logger);

                String metricIngestUri = agentConfig.getValue(METRIC_INGEST_URI);
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
