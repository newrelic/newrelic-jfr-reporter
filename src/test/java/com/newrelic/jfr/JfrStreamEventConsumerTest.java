package com.newrelic.jfr;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Count;
import com.newrelic.telemetry.metrics.Gauge;
import com.newrelic.telemetry.metrics.Metric;
import com.newrelic.telemetry.metrics.MetricBuffer;
import java.util.List;
import jdk.jfr.consumer.RecordedEvent;
import org.junit.jupiter.api.Test;

class JfrStreamEventConsumerTest {

  @Test
  void testApplyMetrics() throws NoSuchMethodException {
    EventMapper eventMapper = mock(EventMapper.class);
    MetricBuffer metricBuffer = mock(MetricBuffer.class);
    RecordedEvent event = mock(RecordedEvent.class);
    JfrStreamEventConsumer testClass = new JfrStreamEventConsumer(eventMapper, metricBuffer);
    Metric countMetric = new Count("foo", 2.0, 30, 50, new Attributes());
    Metric gaugeMetric = new Gauge( "bar", 4,566, new Attributes());
    List<Metric> metrics = List.of(gaugeMetric, countMetric);

    doReturn(metrics).when(eventMapper).apply(event);
    testClass.accept(event);

    verify(metricBuffer).addMetric(gaugeMetric);
    verify(metricBuffer).addMetric(countMetric);
    verifyNoMoreInteractions(metricBuffer);
  }
}