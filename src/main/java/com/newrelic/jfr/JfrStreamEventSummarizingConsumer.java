package com.newrelic.jfr;

import com.newrelic.jfr.summarizers.EventSummarizer;
import com.newrelic.telemetry.metrics.MetricBuffer;
import com.newrelic.telemetry.metrics.Summary;
import jdk.jfr.consumer.RecordedEvent;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A very thin binding that applies an EventSummarizer to aggregate a JFR Event into
 * a list of New Relic Summary dimensional metrics. At the end of the summary period
 * it adds those to a MetricBuffer.
 */
public final class JfrStreamEventSummarizingConsumer implements Consumer<RecordedEvent> {

    private static final int SUMMARY_PERIOD = 60;
    private final EventSummarizer summarizer;
    private final Supplier<MetricBuffer> metricBufferSupplier;
    private LocalDateTime lastSent;

    public JfrStreamEventSummarizingConsumer(EventSummarizer summarizer, Supplier<MetricBuffer> metricBufferSupplier) {
        this.summarizer = summarizer;
        this.metricBufferSupplier = metricBufferSupplier;
        this.lastSent = LocalDateTime.now();
    }

    @Override
    public void accept(RecordedEvent event) {
        summarizer.apply(event);
        var metricBuffer = metricBufferSupplier.get();
        if (LocalDateTime.now().isAfter(lastSent.plusSeconds(SUMMARY_PERIOD))) {
            summarizer.summarizeAndReset().forEach(metricBuffer::addMetric);
            lastSent = LocalDateTime.now();
        }
    }
}
