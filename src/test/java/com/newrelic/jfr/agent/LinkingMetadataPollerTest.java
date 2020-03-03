package com.newrelic.jfr.agent;

import com.newrelic.api.agent.Agent;
import com.newrelic.telemetry.Attributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.newrelic.jfr.attributes.AttributeNames.HOSTNAME;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class LinkingMetadataPollerTest {
    private Agent agent;
    private Map<String, String> mockLinkingMetadata;

    @BeforeEach
    void setup() {
        agent = mock(Agent.class, RETURNS_DEEP_STUBS);
        mockLinkingMetadata = new HashMap<>();
    }

    @Test
    void testThrowsNPE() {
        Consumer<Attributes> mockAttributesConsumer = mock(Consumer.class);
        when(agent.getLinkingMetadata()).thenThrow(new NullPointerException("NPE"));

        var testClass = new LinkingMetadataPoller(agent, mockAttributesConsumer);
        assertFalse(testClass.poll());
        verifyNoMoreInteractions(mockAttributesConsumer);
    }

    @Test
    void testReturnsNull() {
        Consumer<Attributes> mockAttributesConsumer = mock(Consumer.class);
        when(agent.getLinkingMetadata()).thenReturn(null);

        var testClass = new LinkingMetadataPoller(agent, mockAttributesConsumer);
        assertFalse(testClass.poll());
        verifyNoMoreInteractions(mockAttributesConsumer);
    }

    @Test
    void testSuccess() {
        var expectedAttributes = new Attributes().put(HOSTNAME, "MockHostname");

        Consumer<Attributes> mockAttributesConsumer = mock(Consumer.class);
        mockLinkingMetadata.put(HOSTNAME, "MockHostname");
        when(agent.getLinkingMetadata()).thenReturn(mockLinkingMetadata);

        var testClass = new LinkingMetadataPoller(agent, mockAttributesConsumer);
        assertTrue(testClass.poll());
        verify(mockAttributesConsumer).accept(expectedAttributes);

        assertTrue(testClass.poll());
        verifyNoMoreInteractions(mockAttributesConsumer);
    }
}
