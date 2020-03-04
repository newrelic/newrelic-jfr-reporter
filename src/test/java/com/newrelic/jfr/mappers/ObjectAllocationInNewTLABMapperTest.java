package com.newrelic.jfr.mappers;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Count;
import jdk.jfr.consumer.RecordedClass;
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
        var now = System.currentTimeMillis();
        var end = now + 1;

        // RecordedThread
        var recordedThread = mock(RecordedThread.class);
        var eventThread = "pool-3-thread-1";
        when(recordedThread.getJavaName()).thenReturn(eventThread);

        // RecordedClass
        var recordedClass = mock(RecordedClass.class);
        var objectClass = "java.lang.String";
        when(recordedClass.getName()).thenReturn(objectClass);

        // RecordedEvent
        var recordedEvent = mock(RecordedEvent.class);
        var startTime = Instant.ofEpochMilli(now);
        var endTime = Instant.ofEpochMilli(end);
        long tlabSize = 206864;
        long allocationSize = 24;
        when(recordedEvent.getStartTime()).thenReturn(startTime);
        when(recordedEvent.getEndTime()).thenReturn(endTime);
        when(recordedEvent.getThread("eventThread")).thenReturn(recordedThread);
        when(recordedEvent.getClass("objectClass")).thenReturn(recordedClass);
        when(recordedEvent.getLong("tlabSize")).thenReturn(tlabSize);
        when(recordedEvent.getLong("allocationSize")).thenReturn(allocationSize);

        // Expected dimensional metric
        var attr = new Attributes()
                .put("thread", eventThread)
                .put("class", objectClass)
                .put("tlabSize", tlabSize);
        var count = new Count("jfr:ObjectAllocationInNewTLAB.allocation", 0.0 + recordedEvent.getLong("allocationSize"), now, end, attr);
        var expected = List.of(count);

        // Assertions
        var testClass = new ObjectAllocationInNewTLABMapper();
        var result = testClass.apply(recordedEvent);
        assertEquals(expected, result);
    }
}
