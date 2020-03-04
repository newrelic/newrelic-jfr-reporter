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

class ThreadAllocationStatisticsMapperTest {

    @Test
    void testMapper() {
        var now = System.currentTimeMillis();

        // RecordedThread
        var recordedThread = mock(RecordedThread.class);
        var threadName = "main";
        var threadJavaId = 1L;
        var threadOsName = "main";
        when(recordedThread.getJavaName()).thenReturn(threadName);
        when(recordedThread.getJavaThreadId()).thenReturn(threadJavaId);
        when(recordedThread.getOSName()).thenReturn(threadOsName);

        // RecordedEvent
        var recordedEvent = mock(RecordedEvent.class);
        var startTime = Instant.ofEpochMilli(now);
        var allocated = 1250229920d;
        when(recordedEvent.getStartTime()).thenReturn(startTime);
        when(recordedEvent.getDouble("allocated")).thenReturn(allocated);
        when(recordedEvent.getValue("thread")).thenReturn(recordedThread);

        // Expected dimensional metric
        var attr = new Attributes()
                .put("thread.name", threadName)
                .put("thread.javaId", threadJavaId)
                .put("thread.osName", threadOsName);
        var gauge = new Gauge("jfr:ThreadAllocationStatistics.allocated", allocated, now, attr);
        var expected = List.of(gauge);

        // Assertions
        var testClass = new ThreadAllocationStatisticsMapper();
        var result = testClass.apply(recordedEvent);
        assertEquals(expected, result);
    }
}
