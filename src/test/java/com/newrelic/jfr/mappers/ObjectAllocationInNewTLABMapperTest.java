package com.newrelic.jfr.mappers;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Gauge;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedThread;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ObjectAllocationInNewTLABMapperTest {

    @Test
    void testMapper() {
        var eventThread = "pool-3-thread-1";
        var now = System.currentTimeMillis();
        var startTime = Instant.ofEpochMilli(now);
        long allocationSize = 24;
        long tlabSize = 90210;
        var attr = new Attributes().put("thread", eventThread);
        var allocation = new Gauge("jfr:ObjectAllocationInNewTLAB.allocation", 0.0 + allocationSize, now, attr);
        var tlab = new Gauge("jfr:ObjectAllocationInNewTLAB.tlabSize", 0.0 + tlabSize, now, attr);
        var expected = List.of(tlab, allocation);

        var recordedThread = mock(RecordedThread.class);
        var recordedEvent = mock(RecordedEvent.class);

        when(recordedThread.getJavaName()).thenReturn(eventThread);
        when(recordedEvent.getStartTime()).thenReturn(startTime);
        when(recordedEvent.getThread("eventThread")).thenReturn(recordedThread);
        when(recordedEvent.getLong("tlabSize")).thenReturn(tlabSize);
        when(recordedEvent.getLong("allocationSize")).thenReturn(allocationSize);

        var testClass = new ObjectAllocationInNewTLABMapper();

        var result = testClass.apply(recordedEvent);
        assertEquals(expected, result);
    }
}
