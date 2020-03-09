package com.newrelic.jfr.summarizers;

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

class ObjectAllocationOutsideTLABMapperTest {

    final String eventThread = "pool-2-thread-1";
    final long now = System.currentTimeMillis();
    final Instant startTime = Instant.ofEpochMilli(now);
    final long allocationSize = 336L;
    final Attributes attr = new Attributes().put("thread.name", eventThread);

    RecordedEvent recordedEvent;

    @BeforeEach
    void setup() {
        recordedEvent = mock(RecordedEvent.class);
        when(recordedEvent.getStartTime()).thenReturn(startTime);
        when(recordedEvent.getLong("allocationSize")).thenReturn(allocationSize);
    }

//    @Test
    void testMapper() {
        var gauge = new Gauge("jfr:ObjectAllocationOutsideTLAB.allocation", 0.0 + allocationSize, now, attr);
        var expected = List.of(gauge);

        var recordedThread = mock(RecordedThread.class);

        when(recordedThread.getJavaName()).thenReturn(eventThread);
        when(recordedEvent.getValue("eventThread")).thenReturn(recordedThread);

//        var testClass = new ObjectAllocationOutsideTLABMapper();
//        var result = testClass.apply(recordedEvent);
//        assertEquals(expected, result);
    }

//    @Test
    void testWorkaround() {
        var altAttr = new Attributes();
        var gauge = new Gauge("jfr:ObjectAllocationOutsideTLAB.allocation", 0.0 + allocationSize, now, altAttr);
        var expected = List.of(gauge);

        when(recordedEvent.getValue("eventThread")).thenReturn(new Object[]{"one", 23, "what"});

//        var testClass = new ObjectAllocationOutsideTLABMapper();
//        var result = testClass.apply(recordedEvent);
//        assertEquals(expected, result);
    }
}

