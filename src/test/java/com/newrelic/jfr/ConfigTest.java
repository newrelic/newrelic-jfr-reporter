/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.newrelic.jfr;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    @Test
    void testUriDefaults() throws Exception {
        Config testClass = Config.builder().build();
        assertEquals(Config.DEFAULT_METRIC_INGEST_URI, (testClass.getMetricIngestUri()));
    }

    @Test
    void testUriOverride() throws Exception {
        URI uri = URI.create("http://newrelic.com/v99/something");
        Config testClass = Config.builder().metricsIngestUri(uri).build();
        assertEquals(uri, (testClass.getMetricIngestUri()));
    }

}