package com.newrelic.jfr.attributes;

import com.newrelic.telemetry.Attributes;

import java.util.function.Supplier;

//import static com.newrelic.jfr.attributes.AttributeNames.SPAN_ID;
//import static com.newrelic.jfr.attributes.AttributeNames.TRACE_ID;
//import static com.newrelic.jfr.attributes.LinkingMetadata.getSpanId;
//import static com.newrelic.jfr.attributes.LinkingMetadata.getTraceId;

public class BaseAttributes implements Supplier<Attributes> {
    @Override
    public Attributes get() {
        Attributes baseAttributes = new Attributes();
        // TODO do we want to add span.id and trace.id to each metric? Use case?
//        String spanId = getSpanId();
//        String traceId = getTraceId();
//
//        if (spanId != null && !spanId.isEmpty()) {
//            baseAttributes.put(SPAN_ID, spanId);
//        }
//
//        if (traceId != null && !traceId.isEmpty()) {
//            baseAttributes.put(TRACE_ID, traceId);
//        }

        return baseAttributes;
    }
}
