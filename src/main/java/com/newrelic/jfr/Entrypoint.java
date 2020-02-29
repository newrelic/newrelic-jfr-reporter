package com.newrelic.jfr;

import com.newrelic.api.agent.Agent;
import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.jfr.attributes.CommonAttributes;

import java.lang.instrument.Instrumentation;
import java.net.URI;
import java.util.logging.Level;

import static com.newrelic.jfr.Config.INSERT_API_KEY;
import static com.newrelic.jfr.Config.METRIC_INGEST_URI;

public class Entrypoint {
    public static void premain(String agentArgs, Instrumentation inst) {
        Agent agent = NewRelic.getAgent();
        Logger logger = agent.getLogger();
        logger.log(Level.INFO, "Attaching New Relic JFR Monitor");

        try {
            var agentConfig = agent.getConfig();
            String insertApiKey = agentConfig.getValue(INSERT_API_KEY);
            CommonAttributes commonAttributes = new CommonAttributes(agent);

            var builder = Config.builder()
                    .insertApiKey(insertApiKey)
                    .commonAttributes(commonAttributes.get())
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
    }
}
