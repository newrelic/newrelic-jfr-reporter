package com.newrelic.jfr.mappers;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Gauge;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedThread;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ObjectAllocationInNewTLABMapperTest {

    final String eventThread = "pool-3-thread-1";
    final long now = System.currentTimeMillis();
    final Instant startTime = Instant.ofEpochMilli(now);
    final long allocationSize = 24;
    final long tlabSize = 90210;
    final Attributes attr = new Attributes().put("thread", eventThread);

    RecordedEvent recordedEvent;

    @BeforeEach
    void setup() {
        recordedEvent = mock(RecordedEvent.class);
        when(recordedEvent.getStartTime()).thenReturn(startTime);
        when(recordedEvent.getLong("tlabSize")).thenReturn(tlabSize);
        when(recordedEvent.getLong("allocationSize")).thenReturn(allocationSize);
    }

    @Test
    void testMapper() {
        var allocation = new Gauge("jfr:ObjectAllocationInNewTLAB.allocation", 0.0 + allocationSize, now, attr);
        var tlab = new Gauge("jfr:ObjectAllocationInNewTLAB.tlabSize", 0.0 + tlabSize, now, attr);
        var expected = List.of(tlab, allocation);

        var recordedThread = mock(RecordedThread.class);

        when(recordedThread.getJavaName()).thenReturn(eventThread);
        when(recordedEvent.getValue("eventThread")).thenReturn(recordedThread);

        var testClass = new ObjectAllocationInNewTLABMapper();

        var result = testClass.apply(recordedEvent);
        assertEquals(expected, result);
    }

    @Test
    void testWorkaround() {
        Attributes attr = new Attributes();
        var allocation = new Gauge("jfr:ObjectAllocationInNewTLAB.allocation", 0.0 + allocationSize, now, attr);
        var tlab = new Gauge("jfr:ObjectAllocationInNewTLAB.tlabSize", 0.0 + tlabSize, now, attr);
        var expected = List.of(tlab, allocation);

        var recordedThread = mock(RecordedThread.class);

        when(recordedThread.getJavaName()).thenReturn(eventThread);
        when(recordedEvent.getValue("eventThread")).thenReturn(new Object[]{"foo", "21", 44, "baz"});

        var testClass = new ObjectAllocationInNewTLABMapper();

        var result = testClass.apply(recordedEvent);
        assertEquals(expected, result);
    }
}
