package com.newrelic.jfr;

import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingStream;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JfrMonitorTest {

    @Test
    void testStartWiresUpConsumerThatHandlesCPUEvent() throws Exception {
        var latch = new CountDownLatch(1);
        var cpuEventConsumer = new JfrStreamEventConsumer(null, null){
            @Override
            public void accept(RecordedEvent event) {
                latch.countDown();
            }
        };
        var recordingStream = new RecordingStream();
        Supplier<RecordingStream> rsSupplier = () -> recordingStream;
        var testClass = new JfrMonitor(cpuEventConsumer, rsSupplier);

        testClass.start();;
        assertTrue(latch.await(10, TimeUnit.SECONDS));
    }
}