package com.newrelic.jfr.attributes;

import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;

import java.util.Map;
import java.util.logging.Level;

/**
 * A map of linking metadata returned by the agent potentially containing the following:
 * trace.id, span.id, hostname, entity.name, entity.type, entity.guid
 *
 * The entity attributes automatically get added to dimensional metrics as nr.entity.* prefixed
 * values by a backend service, so we omit them in the reporter.
 */
public class LinkingMetadata {
    public static final String TRACE_ID = "trace.id";
    public static final String SPAN_ID = "span.id";
    public static final String HOSTNAME = "hostname";

    /**
     * Gets an opaque map of key/value pairs that can be used to correlate this application in the New Relic backend.
     * Will throw a NullPointerException if RPMService is not yet running.
     *
     * @return Map<String, String> of linking metadata
     */
    public static Map<String, String> getLinkingMetadata() {
        return NewRelic.getAgent().getLinkingMetadata();
    }

    public static boolean agentHasLinkingMetadata() {
        try {
            return !getLinkingMetadata().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public static void waitForAgentInitialization(Logger logger) throws InterruptedException {
        while (true) {
            if (agentHasLinkingMetadata()) {
                logger.log(Level.INFO, "Java Agent now has linking data, JFR Extension can start.");
                break;
            }
            Thread.sleep(1000);
            logger.log(Level.INFO, "JFR Extension is waiting for the agent to finish connecting");
        }
    }

    public static String getTraceId() {
        return getLinkingMetadata().getOrDefault(TRACE_ID, "");
    }

    public static String getSpanId() {
        return getLinkingMetadata().getOrDefault(SPAN_ID, "");
    }

    public static String getHostname() {
        return getLinkingMetadata().getOrDefault(HOSTNAME, "");
    }
}
