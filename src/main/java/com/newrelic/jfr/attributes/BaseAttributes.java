package com.newrelic.jfr.attributes;

import com.newrelic.api.agent.Agent;
import com.newrelic.telemetry.Attributes;

import java.util.function.Supplier;
import java.util.logging.Level;

import static com.newrelic.jfr.attributes.AttributeNames.SPAN_ID;
import static com.newrelic.jfr.attributes.AttributeNames.TRACE_ID;

public class BaseAttributes implements Supplier<Attributes> {
//    Agent agent;
//
//    public BaseAttributes(Agent agent) {
//        this.agent = agent;
//    }

    @Override
    public Attributes get() {
        Attributes baseAttributes = new Attributes();
//        try {
//            // linkingMetadata could be null if RPMService hasn't started..
//            var linkingMetadata = agent.getLinkingMetadata();
//            // TODO do we want to add span.id and trace.id to each metric? Use case?
//            String spanId = linkingMetadata.getOrDefault(SPAN_ID, "");
//            String traceId = linkingMetadata.getOrDefault(TRACE_ID, "");
//
//            if (spanId != null && !spanId.isEmpty()) {
//                baseAttributes.put(SPAN_ID, spanId);
//            }
//
//            if (traceId != null && !traceId.isEmpty()) {
//                baseAttributes.put(TRACE_ID, traceId);
//            }
//        } catch (Throwable t) {
//            agent.getLogger().log(Level.SEVERE, t, "New Relic JFR Monitor is unable to get agent linking metadata.");
//        }
        return baseAttributes;
    }
}
