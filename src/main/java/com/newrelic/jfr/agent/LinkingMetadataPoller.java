package com.newrelic.jfr.agent;

import static com.newrelic.jfr.attributes.AttributeNames.ENTITY_GUID;
import static com.newrelic.jfr.attributes.AttributeNames.HOSTNAME;

import com.newrelic.api.agent.Agent;
import com.newrelic.jfr.MetricNames;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;

class LinkingMetadataPoller {
    public static final String HOSTNAME_KEY = "hostname";
    public static final String ENTITY_GUID_KEY = "entity.guid";
    public static final String UNKNOWN_HOST = "unknown";
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
            if (haveNecessaryMetadata(linkingMetadata)) {
                var hostname = linkingMetadata.get(HOSTNAME_KEY);
                if(hostname == null || (hostname.isEmpty())){
                    hostname = UNKNOWN_HOST;
                }
                var entityGuid = linkingMetadata.get(ENTITY_GUID_KEY);
                var attrMap = new HashMap<String, String>();
                attrMap.put(HOSTNAME, hostname);
                attrMap.put(ENTITY_GUID, entityGuid);
                attributesListener.accept(attrMap);
                gotLinkingMetadata.set(true);
                return true;
            }
        } catch (Exception e) {
          agent.getMetricAggregator()
              .incrementCounter(MetricNames.SUPPORTABILITY_JFR_LINKING_METADATA_FAILED);
            agent.getLogger().log(Level.FINEST, "New Relic JFR Monitor failed to get agent linking metadata. " +
                    "Another attempt will be made when the agent is fully initialized.");
        }
        return false;
    }

    private boolean haveNecessaryMetadata(Map<String, String> linkingMetadata) {
        return !linkingMetadata.isEmpty() &&
                (linkingMetadata.get(ENTITY_GUID_KEY) != null) &&
                (!linkingMetadata.get(ENTITY_GUID_KEY).isEmpty());
    }
}
