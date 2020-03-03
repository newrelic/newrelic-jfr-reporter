package com.newrelic.jfr.agent;

import com.newrelic.api.agent.Agent;
import com.newrelic.api.agent.Config;
import com.newrelic.telemetry.Attributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static com.newrelic.jfr.attributes.AttributeNames.APP_NAME;
import static com.newrelic.jfr.attributes.AttributeNames.SERVICE_NAME;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class AppNamePollerTest {
    private Agent agent;

    @BeforeEach
    void setup() {
        agent = mock(Agent.class, RETURNS_DEEP_STUBS);
    }

    @Test
    void testNullAppName() {
        Consumer<Attributes> mockAttributesConsumer = mock(Consumer.class);
        var mockAgentConfig = mock(Config.class);
        when(agent.getConfig()).thenReturn(mockAgentConfig);
        when(mockAgentConfig.getValue("app_name")).thenReturn(null);

        var testClass = new AppNamePoller(agent, mockAttributesConsumer);
        assertFalse(testClass.poll());
        verifyNoMoreInteractions(mockAttributesConsumer);
    }

    @Test
    void testEmptyStringAppName() {
        Consumer<Attributes> mockAttributesConsumer = mock(Consumer.class);
        var mockAgentConfig = mock(Config.class);
        when(agent.getConfig()).thenReturn(mockAgentConfig);
        when(mockAgentConfig.getValue("app_name")).thenReturn("");

        var testClass = new AppNamePoller(agent, mockAttributesConsumer);
        assertFalse(testClass.poll());
        verifyNoMoreInteractions(mockAttributesConsumer);
    }

    @Test
    void testSuccess() {
        var expectedAttributes = new Attributes().put(APP_NAME, "MockAppName").put(SERVICE_NAME, "MockAppName");

        Consumer<Attributes> mockAttributesConsumer = mock(Consumer.class);
        var mockAgentConfig = mock(Config.class);
        when(agent.getConfig()).thenReturn(mockAgentConfig);
        when(mockAgentConfig.getValue("app_name")).thenReturn("MockAppName");

        var testClass = new AppNamePoller(agent, mockAttributesConsumer);
        assertTrue(testClass.poll());

        verify(mockAttributesConsumer).accept(expectedAttributes);

        assertTrue(testClass.poll());
        verifyNoMoreInteractions(mockAttributesConsumer);
    }
}
