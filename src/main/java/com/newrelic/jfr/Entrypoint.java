package com.newrelic.jfr;

import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.telemetry.Attributes;

import java.lang.instrument.Instrumentation;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.logging.Level;

public class Entrypoint {
    public static void premain(String agentArgs, Instrumentation inst) {
        Logger logger = NewRelic.getAgent().getLogger();
        var agentConfig = NewRelic.getAgent().getConfig();
        var isJfrEnabled = new AgentJfrConfigSetting(agentConfig).isJfrEnabled();

        if(!isJfrEnabled){
            logger.log(Level.INFO, "JFR Monitor is disabled: JFR config has not been enabled in the Java agent.");
            return;
        }

        logger.log(Level.INFO, "Attaching New Relic JFR Monitor");

        try {
            String appName = agentConfig.getValue("app_name");
            var commonAttributes = new Attributes()
                    .put("host", getHostName())
                    .put("appName", appName)
                    .put("service.name", appName)
                    .put("instrumentation.name", "JFR")
                    .put("instrumentation.provider", "JFR Agent Extension")
                    .put("collector.name", "JFR Agent Extension");

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
    }

    static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }
}
