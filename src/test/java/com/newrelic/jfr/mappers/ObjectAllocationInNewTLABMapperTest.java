package com.newrelic.jfr.mappers;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Count;
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
        var recordedThread = mock(RecordedThread.class);
        var eventThread = "pool-3-thread-1";

        var recordedEvent = mock(RecordedEvent.class);
        var now = System.currentTimeMillis();
        var end = now + 1;
        var startTime = Instant.ofEpochMilli(now);
        var endTime = Instant.ofEpochMilli(end);
        long allocationSize = 24;

        var attr = new Attributes().put("thread", eventThread);
        var count = new Count("jfr:ObjectAllocationInNewTLAB.allocation", 0.0 + allocationSize, now, end, attr);
        var expected = List.of(count);

        var testClass = new ObjectAllocationInNewTLABMapper();

        when(recordedThread.getJavaName()).thenReturn(eventThread);

        when(recordedEvent.getStartTime()).thenReturn(startTime);
        when(recordedEvent.getEndTime()).thenReturn(endTime);
        when(recordedEvent.getThread("eventThread")).thenReturn(recordedThread);
        when(recordedEvent.getLong("allocationSize")).thenReturn(allocationSize);

        var result = testClass.apply(recordedEvent);
        assertEquals(expected, result);
    }
}
