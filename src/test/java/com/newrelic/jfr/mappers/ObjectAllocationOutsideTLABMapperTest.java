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
        var recordedThread = mock(RecordedThread.class);
        var eventThread = "pool-2-thread-1";

        var recordedClass = mock(RecordedClass.class);
        var objectClass = "java.util.GregorianCalendar";

        var recordedEvent = mock(RecordedEvent.class);
        var now = System.currentTimeMillis();
        var end = now + 1;
        var startTime = Instant.ofEpochMilli(now);
        var endTime = Instant.ofEpochMilli(end);
        var allocationSize = 336L;

        var attr = new Attributes()
                .put("thread.name", eventThread)
                .put("class", objectClass);
        var count = new Count("jfr:ObjectAllocationOutsideTLAB.allocation", 0.0 + allocationSize, now, end, attr);
        var expected = List.of(count);

        var testClass = new ObjectAllocationOutsideTLABMapper();

        when(recordedThread.getJavaName()).thenReturn(eventThread);

        when(recordedClass.getName()).thenReturn(objectClass);

        when(recordedEvent.getStartTime()).thenReturn(startTime);
        when(recordedEvent.getEndTime()).thenReturn(endTime);
        when(recordedEvent.getValue("eventThread")).thenReturn(recordedThread);
        when(recordedEvent.getClass("objectClass")).thenReturn(recordedClass);
        when(recordedEvent.getLong("allocationSize")).thenReturn(allocationSize);

        var result = testClass.apply(recordedEvent);
        assertEquals(expected, result);
    }
}
