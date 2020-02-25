package com.newrelic.jfr;

import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.telemetry.Attributes;

import java.lang.instrument.Instrumentation;
import java.net.URI;
import java.util.logging.Level;

public class Entrypoint {
    public static void premain(String agentArgs, Instrumentation inst) {
        Logger logger = NewRelic.getAgent().getLogger();
        logger.log(Level.INFO, "Attaching New Relic JFR Monitor");
        Object appName = NewRelic.getAgent().getConfig().getValue("app_name");

        try {
            var commonAttributes = new Attributes()
                    .put("appName", appName.toString())
                    .put("instrumentation.name", "JFR")
                    .put("instrumentation.provider", "JFR Agent Extension")
                    .put("collector.name", "JFR Agent Extension");

            var agentConfig = NewRelic.getAgent().getConfig();
            String insertApiKey = agentConfig.getValue("insert_api_key");
            String metricIngestUri = agentConfig.getValue("jfr.metric_ingest_uri");

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
    }
}
