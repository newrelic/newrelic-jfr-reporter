package com.newrelic.jfr.attributes;

import com.newrelic.telemetry.Attributes;

import java.util.function.Supplier;

import static com.newrelic.jfr.attributes.AttributeUtil.SPAN_ID;
import static com.newrelic.jfr.attributes.AttributeUtil.TRACE_ID;
import static com.newrelic.jfr.attributes.LinkingMetadata.getSpanId;
import static com.newrelic.jfr.attributes.LinkingMetadata.getTraceId;

public class BaseAttributes implements Supplier<Attributes> {
    @Override
    public Attributes get() {
        return new Attributes()
                // TODO do we want to add span.id and trace.id to each metric? Should we omit them if they're empty?
                .put(SPAN_ID, getSpanId())
                .put(TRACE_ID, getTraceId());
    }
}
