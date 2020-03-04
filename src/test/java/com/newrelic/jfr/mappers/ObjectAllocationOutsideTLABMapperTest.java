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

class ObjectAllocationOutsideTLABMapperTest {

    @Test
    void testMapper() {
        var now = System.currentTimeMillis();
        var end = now + 1;

        // RecordedThread
        var recordedThread = mock(RecordedThread.class);
        var eventThread = "pool-2-thread-1";
        when(recordedThread.getJavaName()).thenReturn(eventThread);

        // RecordedClass
        var recordedClass = mock(RecordedClass.class);
        var objectClass = "java.util.GregorianCalendar";
        when(recordedClass.getName()).thenReturn(objectClass);

        // RecordedEvent
        var recordedEvent = mock(RecordedEvent.class);
        var startTime = Instant.ofEpochMilli(now);
        var endTime = Instant.ofEpochMilli(end);
        var allocationSize = 336L;
        when(recordedEvent.getStartTime()).thenReturn(startTime);
        when(recordedEvent.getEndTime()).thenReturn(endTime);
        when(recordedEvent.getValue("eventThread")).thenReturn(recordedThread);
        when(recordedEvent.getClass("objectClass")).thenReturn(recordedClass);
        when(recordedEvent.getLong("allocationSize")).thenReturn(allocationSize);

        // Expected dimensional metric
        var attr = new Attributes()
                .put("thread.name", eventThread)
                .put("class", objectClass);
        var count = new Count("jfr:ObjectAllocationOutsideTLAB.allocation", 0.0 + recordedEvent.getLong("allocationSize"), now, end, attr);
        var expected = List.of(count);

        // Assertions
        var testClass = new ObjectAllocationOutsideTLABMapper();
        var result = testClass.apply(recordedEvent);
        assertEquals(expected, result);
    }
}
