package com.newrelic.jfr.attributes;

import com.newrelic.api.agent.Agent;
import com.newrelic.api.agent.Config;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.telemetry.Attributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.newrelic.jfr.attributes.AttributeNames.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommonAttributesTest {
    private Agent agent;
    private Map<String, String> mockLinkingMetadata;
    private Config mockAgentConfig;

    @BeforeEach
    void setup() {
        agent = NewRelic.getAgent();
        mockLinkingMetadata = new HashMap<>();
    }

    @Test
    void testNoOpAgentExpectedAttributes() {
        Attributes expectedAttributes = new Attributes();
        expectedAttributes.put(COLLECTOR_NAME, "JFR Agent Extension");
        expectedAttributes.put(INSTRUMENTATION_NAME, "JFR");
        expectedAttributes.put(INSTRUMENTATION_PROVIDER, "JFR Agent Extension");

        Attributes actualAttributes = new CommonAttributes(agent).get();
        assertEquals(expectedAttributes, actualAttributes);
    }

    @Test
    void testNoOpAgentUnexpectedEmptyAttributes() {
        Attributes unexpectedAttributes = new Attributes();
        unexpectedAttributes.put(APP_NAME, "");
        unexpectedAttributes.put(HOSTNAME, "");
        unexpectedAttributes.put(HOST, "");
        unexpectedAttributes.put(SERVICE_NAME, "");
        unexpectedAttributes.put(COLLECTOR_NAME, "JFR Agent Extension");
        unexpectedAttributes.put(INSTRUMENTATION_NAME, "JFR");
        unexpectedAttributes.put(INSTRUMENTATION_PROVIDER, "JFR Agent Extension");

        Attributes actualAttributes = new CommonAttributes(agent).get();
        // empty attributes should not be present in actualAttributes
        assertNotEquals(unexpectedAttributes, actualAttributes);
    }

    @Test
    void testMockAgentExpectedAttributes() {
        agent = mock(Agent.class);
        mockLinkingMetadata.put(HOSTNAME, "Mock Host");
        when(agent.getLinkingMetadata()).thenReturn(mockLinkingMetadata);

        mockAgentConfig = mock(Config.class);
        when(agent.getConfig()).thenReturn(mockAgentConfig);
        when(mockAgentConfig.getValue("app_name")).thenReturn("Mock App Name");

        Attributes expectedAttributes = new Attributes();
        expectedAttributes.put(APP_NAME, "Mock App Name");
        expectedAttributes.put(SERVICE_NAME, "Mock App Name");
        expectedAttributes.put(HOSTNAME, "Mock Host");
        expectedAttributes.put(HOST, "Mock Host");
        expectedAttributes.put(COLLECTOR_NAME, "JFR Agent Extension");
        expectedAttributes.put(INSTRUMENTATION_NAME, "JFR");
        expectedAttributes.put(INSTRUMENTATION_PROVIDER, "JFR Agent Extension");

        Attributes actualAttributes = new CommonAttributes(agent).get();
        assertEquals(expectedAttributes, actualAttributes);
    }

    @Test
    void testMockAgentUnexpectedEmptyAttributes() {
        agent = mock(Agent.class);
        mockLinkingMetadata.put(HOSTNAME, "");
        when(agent.getLinkingMetadata()).thenReturn(mockLinkingMetadata);

        mockAgentConfig = mock(Config.class);
        when(agent.getConfig()).thenReturn(mockAgentConfig);
        when(mockAgentConfig.getValue("app_name")).thenReturn("");

        Attributes unexpectedAttributes = new Attributes();
        unexpectedAttributes.put(APP_NAME, "");
        unexpectedAttributes.put(SERVICE_NAME, "");
        unexpectedAttributes.put(HOSTNAME, "");
        unexpectedAttributes.put(HOST, "");
        unexpectedAttributes.put(COLLECTOR_NAME, "JFR Agent Extension");
        unexpectedAttributes.put(INSTRUMENTATION_NAME, "JFR");
        unexpectedAttributes.put(INSTRUMENTATION_PROVIDER, "JFR Agent Extension");

        Attributes actualAttributes = new CommonAttributes(agent).get();
        // empty attributes should not be present in actualAttributes
        assertNotEquals(unexpectedAttributes, actualAttributes);

        Attributes expectedAttributes = new Attributes();
        expectedAttributes.put(COLLECTOR_NAME, "JFR Agent Extension");
        expectedAttributes.put(INSTRUMENTATION_NAME, "JFR");
        expectedAttributes.put(INSTRUMENTATION_PROVIDER, "JFR Agent Extension");
        assertEquals(expectedAttributes, actualAttributes);
    }
}
