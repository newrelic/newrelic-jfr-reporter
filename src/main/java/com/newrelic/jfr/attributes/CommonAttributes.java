package com.newrelic.jfr.attributes;

import com.newrelic.api.agent.Agent;
import com.newrelic.telemetry.Attributes;

import java.util.function.Supplier;
import java.util.logging.Level;

import static com.newrelic.jfr.attributes.AttributeNames.*;

public class CommonAttributes implements Supplier<Attributes> {
    Agent agent;

    public CommonAttributes(Agent agent) {
        this.agent = agent;
    }

    @Override
    public Attributes get() {
        Attributes commonAttributes = new Attributes()
                .put(INSTRUMENTATION_NAME, "JFR")
                .put(INSTRUMENTATION_PROVIDER, "JFR Agent Extension")
                .put(COLLECTOR_NAME, "JFR Agent Extension");

        String appName = agent.getConfig().getValue("app_name");
        if (appName != null && !appName.isEmpty()) {
            // TODO do we need both appName and service.name as attributes?
            commonAttributes
                    .put(APP_NAME, appName)
                    .put(SERVICE_NAME, appName);
        }

        try {
            // linkingMetadata could be null if RPMService hasn't started..
            var linkingMetadata = agent.getLinkingMetadata();
            final var hostname = linkingMetadata.getOrDefault(HOSTNAME, "");
            if (hostname != null && !hostname.isEmpty()) {
                // TODO do we need both host and hostname as attributes?
                commonAttributes
                        .put(HOSTNAME, hostname);
            }
        } catch (Throwable t) {
            agent.getLogger().log(Level.WARNING, "New Relic JFR Monitor failed to get agent linking metadata. " +
                    "Another attempt will be made when the agent is fully initialized.");
        }
        return commonAttributes;
    }
}
