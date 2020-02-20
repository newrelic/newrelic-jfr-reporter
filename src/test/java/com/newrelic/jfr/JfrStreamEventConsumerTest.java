package com.newrelic.jfr;

import org.junit.jupiter.api.Test;

class JfrStreamEventConsumerTest {

  @Test
  void testApplyMetrics() throws NoSuchMethodException {
    //FIXME:  Looks like we'll need to fire up JFR for real events...
//    EventMapper eventMapper = mock(EventMapper.class);
//    MetricBuffer metricBuffer = new MetricBuffer(new Attributes());
//
//    JfrStreamEventConsumer testClass = new JfrStreamEventConsumer(eventMapper, metricBuffer);
//
//    Metric countMetric = new Count("fun", 2.0, 30, 50, new Attributes());
//    Metric gaugeMetric = new Gauge( "yikes", 4,566, new Attributes());
//    List<Metric> metrics = List.of(gaugeMetric, countMetric);
//    doReturn(metrics).when(eventMapper).apply(event);
//
//    testClass.accept(event);
//    verify(metricBuffer).addMetric(gaugeMetric);
//    verify(metricBuffer).addMetric(countMetric);
//    verifyNoMoreInteractions(metricBuffer);
  }
}