/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.newrelic.jfr.agent;

import com.newrelic.api.agent.Logger;
import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.MetricBuffer;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * Responsible for handling a change in attributes coming from the agent.
 * It must do the following:
 * + modify/mutate an Attributes instance
 * + create a new MetricBuffer instance using the new Attributes
 * + assign the new MetricBuffer to an AtomicReference
 * + cause any remaining metrics in the old MetricBuffer to be sent
 */
public class AgentAttributesChangeListener implements Consumer<Map<String, String>> {

    private final Logger logger;
    private final Attributes commonAttributes;
    private final AtomicReference<MetricBuffer> metricBufferReference;
    private final Consumer<MetricBuffer> sender;

    public AgentAttributesChangeListener(Logger logger, Attributes commonAttributes, AtomicReference<MetricBuffer> metricBufferReference, Consumer<MetricBuffer> sender) {
        this.logger = logger;
        this.commonAttributes = commonAttributes;
        this.metricBufferReference = metricBufferReference;
        this.sender = sender;
    }

    @Override
    public void accept(Map<String, String> attrs) {
        logger.log(Level.INFO, "Refreshing the MetricBuffer for the New Relic JFR Monitor");

        attrs.forEach(commonAttributes::put);

        var newBuffer = new MetricBuffer(commonAttributes);

        var oldBuffer = metricBufferReference.getAndSet(newBuffer);
        logger.log(Level.INFO, "New Relic JFR Monitor: Sending with remaining old batch...");
        sender.accept(oldBuffer);

    }
}
