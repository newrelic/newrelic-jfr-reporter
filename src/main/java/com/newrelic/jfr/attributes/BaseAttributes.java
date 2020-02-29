package com.newrelic.jfr.attributes;

import com.newrelic.telemetry.Attributes;

import java.util.function.Supplier;

public class BaseAttributes implements Supplier<Attributes> {

    @Override
    public Attributes get() {
        Attributes baseAttributes = new Attributes();
        // TODO do we want to add span.id and trace.id to each metric? Use case?
        return baseAttributes;
    }
}
