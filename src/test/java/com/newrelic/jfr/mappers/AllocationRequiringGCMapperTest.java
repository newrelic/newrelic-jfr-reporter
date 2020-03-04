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

class AllocationRequiringGCMapperTest {

    @Test
    void testMapper() {
        var now = System.currentTimeMillis();
        var end = now + 1;

        // RecordedThread
        var recordedThread = mock(RecordedThread.class);
        var threadId = 46L;
        var eventThread = "Thread-13";
        when(recordedThread.getId()).thenReturn(threadId);
        when(recordedThread.getJavaName()).thenReturn(eventThread);

        // RecordedEvent
        var recordedEvent = mock(RecordedEvent.class);
        var startTime = Instant.ofEpochMilli(now);
        var endTime = Instant.ofEpochMilli(end);
        var size = 32784L;
        var gcId = 35;
        when(recordedEvent.getStartTime()).thenReturn(startTime);
        when(recordedEvent.getEndTime()).thenReturn(endTime);
        when(recordedEvent.getValue("eventThread")).thenReturn(recordedThread);
        when(recordedEvent.getInt("gcId")).thenReturn(gcId);
        when(recordedEvent.getLong("size")).thenReturn(size);

        // Expected dimensional metric
        var attr = new Attributes()
                .put("thread.id", threadId)
                .put("thread.name", eventThread)
                .put("gcId", gcId);
        var gauge = new Gauge("jfr:AllocationRequiringGC.allocationSize", recordedEvent.getLong("size"), now, attr);
        var expected = List.of(gauge);

        // Assertions
        var testClass = new AllocationRequiringGCMapper();
        var result = testClass.apply(recordedEvent);
        assertEquals(expected, result);
    }
}
