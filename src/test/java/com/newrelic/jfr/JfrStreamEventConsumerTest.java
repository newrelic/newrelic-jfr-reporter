package com.newrelic.jfr;

import com.newrelic.jfr.mappers.EventMapper;
import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Count;
import com.newrelic.telemetry.metrics.Gauge;
import com.newrelic.telemetry.metrics.MetricBuffer;
import jdk.jfr.consumer.RecordedEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class JfrStreamEventConsumerTest {

    @Test
    void testApplyMetrics() {
        var eventMapper = mock(EventMapper.class);
        var metricBuffer = mock(MetricBuffer.class);
        var event = mock(RecordedEvent.class);
        var testClass = new JfrStreamEventConsumer(eventMapper, () -> metricBuffer);
        var countMetric = new Count("foo", 2.0, 30, 50, new Attributes());
        var gaugeMetric = new Gauge("bar", 4, 566, new Attributes());
        var metrics = List.of(gaugeMetric, countMetric);

        doReturn(metrics).when(eventMapper).apply(event);
        testClass.accept(event);

        verify(metricBuffer).addMetric(gaugeMetric);
        verify(metricBuffer).addMetric(countMetric);
        verifyNoMoreInteractions(metricBuffer);
    }
}