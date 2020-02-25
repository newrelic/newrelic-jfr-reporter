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

        try {
            var commonAttributes = new Attributes()
                    .put("instrumentation.name", "JFR")
                    .put("instrumentation.provider", "JFR Agent Extension")
                    .put("collector.name", "JFR Agent Extension");

            String apiKey = NewRelic.getAgent().getConfig().getValue("jfr.insert_api_key");
            String ingestUri = NewRelic.getAgent().getConfig().getValue("jfr.ingest_uri");

            Config.Builder builder = Config.builder()
                    .apiKey(apiKey)
                    .commonAttributes(commonAttributes)
                    .logger(logger);

            if (ingestUri != null) {
                builder = builder.metricsIngestUrl(URI.create(ingestUri));
            }
            var config = builder.build();
            new Reporter(config).start();
        } catch (Throwable t) {
            NewRelic.getAgent().getLogger().log(Level.SEVERE, t, "Unable to attach New Relic JFR Monitor");
        }
    }
}
