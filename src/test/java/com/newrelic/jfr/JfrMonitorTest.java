package com.newrelic.jfr;

import com.newrelic.telemetry.metrics.MetricBuffer;
import jdk.jfr.EventSettings;
import jdk.jfr.consumer.RecordingStream;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JfrMonitorTest {

    @Test
    void testStartWiresUpConsumerThatHandlesEvent() throws InterruptedException {
        var latch = new CountDownLatch(1);
        var recordingStream = mock(RecordingStream.class);
        var metricBuffer = mock(MetricBuffer.class);
        var eventSettings = mock(EventSettings.class);
        when(recordingStream.enable("jdk.CPULoad")).thenReturn(eventSettings);
        when(recordingStream.enable("jdk.GCHeapSummary")).thenReturn(eventSettings);
        when(recordingStream.enable("jdk.GarbageCollection")).thenReturn(eventSettings);
        doAnswer(invocationOnMock -> {
            latch.countDown();
            return null;
        }).when(recordingStream).start();

        Supplier<RecordingStream> rsSupplier = () -> recordingStream;
        var testClass = new JfrMonitor(rsSupplier, new MapperRegistry(() -> metricBuffer));

        testClass.start();

        assertTrue(latch.await(10, TimeUnit.SECONDS));
        verify(eventSettings, times(3)).withPeriod(Duration.ofSeconds(1));
    }

}