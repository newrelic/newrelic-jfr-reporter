package com.newrelic.jfr.agent;

import com.newrelic.api.agent.Logger;
import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.MetricBuffer;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.*;

class AgentAttributesChangeListenerTest {

    @Test
    void testNewAttributes() {

        var originalAttributes = new Attributes().put("a", "b");
        var newAttributes = Map.of("foo", "bar", "bar", "baz");
        var expectedAttributes = new Attributes(originalAttributes)
                .put("foo", "bar")
                .put("bar", "baz");

        var logger = mock(Logger.class);
        var buff1 = mock(MetricBuffer.class);
        var sender = mock(Consumer.class);

        var bufferRef = new AtomicReference<>(buff1);

        var testClass = new AgentAttributesChangeListener(logger, originalAttributes, bufferRef, sender);
        testClass.accept(newAttributes);
        assertEquals(expectedAttributes, originalAttributes);   //it was mutated
        assertNotSame(buff1, bufferRef.get());  //new reference assigned
        assertEquals(expectedAttributes, bufferRef.get().createBatch().getCommonAttributes());  //batch contains new attrs
        verify(sender).accept(buff1);   //sender was sent the old buffer to clear out
        verifyNoMoreInteractions(sender);
    }

}