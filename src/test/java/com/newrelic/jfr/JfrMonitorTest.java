package com.newrelic.jfr;

import java.time.Duration;
import jdk.jfr.EventSettings;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingStream;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JfrMonitorTest {

    @Test
    void testStartWiresUpConsumerThatHandlesEvent() throws InterruptedException {
        var latch = new CountDownLatch(1);
        var recordingStream = mock(RecordingStream.class);
        var cpuEventConsumer = mock(JfrStreamEventConsumer.class);
        var eventSettings = mock(EventSettings.class);
        when(recordingStream.enable("jdk.CPULoad")).thenReturn(eventSettings);
        doAnswer(invocationOnMock -> {
            latch.countDown();
            return null;
        }).when(recordingStream).start();

        Supplier < RecordingStream > rsSupplier = () -> recordingStream;
        var testClass = new JfrMonitor(cpuEventConsumer, rsSupplier);

        testClass.start();

        assertTrue(latch.await(10, TimeUnit.SECONDS));
        verify(recordingStream).onEvent("jdk.CPULoad", cpuEventConsumer);
        verify(eventSettings).withPeriod(Duration.ofSeconds(1));
    }

}