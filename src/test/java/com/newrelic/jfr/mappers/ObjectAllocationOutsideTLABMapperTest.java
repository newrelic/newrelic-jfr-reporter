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

class ObjectAllocationOutsideTLABMapperTest {

    @Test
    void testMapper() {
        var eventThread = "pool-2-thread-1";
        var now = System.currentTimeMillis();
        var startTime = Instant.ofEpochMilli(now);
        var allocationSize = 336L;
        var attr = new Attributes().put("thread.name", eventThread);
        var gauge = new Gauge("jfr:ObjectAllocationOutsideTLAB.allocation", 0.0 + allocationSize, now, attr);
        var expected = List.of(gauge);

        var recordedThread = mock(RecordedThread.class);
        var recordedEvent = mock(RecordedEvent.class);

        when(recordedThread.getJavaName()).thenReturn(eventThread);
        when(recordedEvent.getStartTime()).thenReturn(startTime);
        when(recordedEvent.getValue("eventThread")).thenReturn(recordedThread);
        when(recordedEvent.getLong("allocationSize")).thenReturn(allocationSize);

        var testClass = new ObjectAllocationOutsideTLABMapper();
        var result = testClass.apply(recordedEvent);
        assertEquals(expected, result);
    }
}
