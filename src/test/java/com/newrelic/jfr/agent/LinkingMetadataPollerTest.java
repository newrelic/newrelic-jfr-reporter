/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.newrelic.jfr.agent;

import com.newrelic.api.agent.Agent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.newrelic.jfr.attributes.AttributeNames.ENTITY_GUID;
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
        Consumer<Map<String,String>> mockAttributesConsumer = mock(Consumer.class);
        when(agent.getLinkingMetadata()).thenThrow(new NullPointerException("NPE"));

        var testClass = new LinkingMetadataPoller(agent, mockAttributesConsumer);
        assertFalse(testClass.poll());
        verifyNoMoreInteractions(mockAttributesConsumer);
    }

    @Test
    void testReturnsNull() {
        Consumer<Map<String,String>> mockAttributesConsumer = mock(Consumer.class);
        when(agent.getLinkingMetadata()).thenReturn(null);

        var testClass = new LinkingMetadataPoller(agent, mockAttributesConsumer);
        assertFalse(testClass.poll());
        verifyNoMoreInteractions(mockAttributesConsumer);
    }

    @Test
    void testSuccess() {
        var expectedAttributes = Map.of(HOSTNAME, "MockHostname", ENTITY_GUID, "entity");

        Consumer<Map<String,String>> mockAttributesConsumer = mock(Consumer.class);
        mockLinkingMetadata.put("hostname", "MockHostname");
        mockLinkingMetadata.put("entity.guid", "entity");
        when(agent.getLinkingMetadata()).thenReturn(mockLinkingMetadata);

        var testClass = new LinkingMetadataPoller(agent, mockAttributesConsumer);
        assertTrue(testClass.poll());
        verify(mockAttributesConsumer).accept(expectedAttributes);

        assertTrue(testClass.poll());
        verifyNoMoreInteractions(mockAttributesConsumer);
    }

    @Test
    void testDelayedSuccess() {
        var expectedAttributes = Map.of(HOSTNAME, "MockHostname", ENTITY_GUID, "entity");

        Consumer<Map<String,String>> mockAttributesConsumer = mock(Consumer.class);
        when(agent.getLinkingMetadata()).thenReturn(mockLinkingMetadata);

        var testClass = new LinkingMetadataPoller(agent, mockAttributesConsumer);

        assertFalse(testClass.poll());

        mockLinkingMetadata.put("hostname", "MockHostname");
        mockLinkingMetadata.put("entity.guid", "entity");

        assertTrue(testClass.poll());
        verify(mockAttributesConsumer).accept(expectedAttributes);

        assertTrue(testClass.poll());
        verifyNoMoreInteractions(mockAttributesConsumer);
    }

    @Test
    void testTriesUntilEntityGuidPresent() {
        var expectedAttributes = Map.of(HOSTNAME, "unknown", ENTITY_GUID, "bbbaaacccaaa");

        Consumer<Map<String,String>> mockAttributesConsumer = mock(Consumer.class);
        when(agent.getLinkingMetadata()).thenReturn(mockLinkingMetadata);

        var testClass = new LinkingMetadataPoller(agent, mockAttributesConsumer);

        assertFalse(testClass.poll());

        mockLinkingMetadata.put("hostname", null);

        assertFalse(testClass.poll());
        verifyNoInteractions(mockAttributesConsumer);

        mockLinkingMetadata.put("entity.guid", "bbbaaacccaaa");
        assertTrue(testClass.poll());
        verify(mockAttributesConsumer).accept(expectedAttributes);

        assertTrue(testClass.poll());
        verifyNoMoreInteractions(mockAttributesConsumer);
    }

}
