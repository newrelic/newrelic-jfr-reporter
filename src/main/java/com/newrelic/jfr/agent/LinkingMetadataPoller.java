package com.newrelic.jfr.agent;

import com.newrelic.api.agent.Agent;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;

import static com.newrelic.jfr.attributes.AttributeNames.HOSTNAME;

class LinkingMetadataPoller {
    private final AtomicBoolean gotLinkingMetadata = new AtomicBoolean(false);
    private final Agent agent;
    private final Consumer<Map<String,String>> attributesListener;

    public LinkingMetadataPoller(Agent agent,  Consumer<Map<String,String>> attributesListener) {
        this.agent = agent;
        this.attributesListener = attributesListener;
    }

    boolean poll() {
        if (gotLinkingMetadata.get()) {
            return true;
        }
        try {
            Map<String, String> linkingMetadata = agent.getLinkingMetadata();
            if (linkingMetadata != null) {
                var hostname = linkingMetadata.getOrDefault("hostname", "");
                if (hostname != null && !hostname.isEmpty()) {
                    gotLinkingMetadata.set(true);
                    attributesListener.accept(Map.of(HOSTNAME, hostname));
                    return true;
                }
            }
        } catch (Exception e) {
            agent.getLogger().log(Level.FINEST, "New Relic JFR Monitor failed to get agent linking metadata. " +
                    "Another attempt will be made when the agent is fully initialized.");
        }
        return false;
    }
}
