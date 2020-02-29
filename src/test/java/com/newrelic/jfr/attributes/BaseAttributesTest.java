package com.newrelic.jfr.attributes;

import com.newrelic.api.agent.Agent;
import com.newrelic.api.agent.Config;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.telemetry.Attributes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BaseAttributesTest {
    private Agent agent;
    private Map<String, String> mockLinkingMetadata;
    private Config mockAgentConfig;

    @BeforeEach
    void setup() {
        agent = NewRelic.getAgent();
        mockLinkingMetadata = new HashMap<>();
    }

    @Test
    void testExpectedAttributes() {
        Attributes expectedAttributes = new Attributes();
        Attributes actualAttributes = new BaseAttributes().get();
        // world's least useful test, currently there are no base attributes to test
        assertEquals(expectedAttributes, actualAttributes);
    }
}
