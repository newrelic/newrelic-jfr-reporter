/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.newrelic.jfr;

import com.newrelic.telemetry.metrics.MetricBuffer;
import jdk.jfr.EventSettings;
import jdk.jfr.consumer.RecordingStream;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
        var mapperRegistry = ToMetricRegistry.createDefault();
        var summarizerRegistry = ToSummaryRegistry.createDefault();

        var recordingStream = mock(RecordingStream.class);
        var metricBuffer = mock(MetricBuffer.class);
        var eventSettings = mock(EventSettings.class);
        var expectedWithPeriodCalls = new AtomicInteger(0);
        mapperRegistry.all().forEach(toMetric -> {
            when(recordingStream.enable(toMetric.getEventName())).thenReturn(eventSettings);
            if (toMetric.getPollingDuration().isPresent()) {
                expectedWithPeriodCalls.incrementAndGet();
            }
        });
        doAnswer(invocationOnMock -> {
            latch.countDown();
            return null;
        }).when(recordingStream).start();

        var testClass = new JfrMonitor(mapperRegistry, summarizerRegistry, () -> metricBuffer, () -> recordingStream);

        testClass.start();

        assertTrue(latch.await(10, TimeUnit.SECONDS));
        verify(eventSettings, times(expectedWithPeriodCalls.get())).withPeriod(Duration.ofSeconds(1));
    }

}