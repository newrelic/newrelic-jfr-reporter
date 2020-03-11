package com.newrelic.jfr.summarizers;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Metric;
import com.newrelic.telemetry.metrics.Summary;
import jdk.jfr.consumer.RecordedEvent;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class G1GarbageCollectionSummarizerTest {

    @Test
    void testSingleEventSummary() {
        var numOfEvents = 1;

        var summaryStartTime = Instant.now().toEpochMilli();
        var event1StartTime = summaryStartTime + numOfEvents;
        var event1DurationNanos = 13700000;
        var event1DurationMillis = Duration.ofNanos(event1DurationNanos).toMillis();

        var expectedSummaryEvent = new Summary(
                "jfr:G1GarbageCollection.duration",
                numOfEvents, // count
                event1DurationMillis, // sum
                event1DurationMillis, // min
                event1DurationMillis, // max
                summaryStartTime, // startTimeMs
                event1StartTime, // endTimeMs: the summary metric endTimeMs is the eventStartTime of each RecordedEvent
                new Attributes());

        var event = mock(RecordedEvent.class);
        List<Metric> expected = List.of(expectedSummaryEvent);
        var testClass = new G1GarbageCollectionSummarizer(summaryStartTime);

        when(event.getStartTime()).thenReturn(Instant.ofEpochMilli(event1StartTime));
        when(event.getDuration("duration")).thenReturn(Duration.ofNanos(event1DurationNanos));

        testClass.apply(event);
        final List<Summary> result = testClass.summarizeAndReset().collect(toList());

        assertEquals(expected, result);
    }

    @Test
    void testMultipleEventSummaryAndReset() {
        final var defaultCount = 0;
        final var defaultSum = Duration.ofNanos(0L).toMillis();
        final var defaultMin = Duration.ofNanos(Long.MAX_VALUE).toMillis();
        final var defaultMax = Duration.ofNanos(0L).toMillis();

        var summaryStartTime = Instant.now().toEpochMilli();

        var numOfEvents = 1;
        var event1 = mock(RecordedEvent.class);
        // add numOfEvents to make the event start time later than the summary
        var event1StartTime = summaryStartTime + numOfEvents;
        var event1DurationNanos = 13700000;

        numOfEvents = ++numOfEvents;
        var event2 = mock(RecordedEvent.class);
        // add numOfEvents to make the event start time later than the summary and previous event
        var event2StartTime = summaryStartTime + numOfEvents;
        var event2DurationNanos = 24800000; // max duration of final summary
        var event2DurationMillis = Duration.ofNanos(event2DurationNanos).toMillis();

        numOfEvents = ++numOfEvents;
        var event3 = mock(RecordedEvent.class);
        // add numOfEvents to make the event start time later than the summary and previous event
        var event3StartTime = summaryStartTime + numOfEvents;
        var event3DurationNanos = 1000000; // min duration of final summary
        var event3DurationMillis = Duration.ofNanos(event3DurationNanos).toMillis();

        var summedDurationNanos = event1DurationNanos + event2DurationNanos + event3DurationNanos;
        var summedDurationMillis = Duration.ofNanos(summedDurationNanos).toMillis();

        var expectedSummary = new Summary(
                "jfr:G1GarbageCollection.duration",
                numOfEvents, // count
                summedDurationMillis, // sum
                event3DurationMillis, // min
                event2DurationMillis, // max
                summaryStartTime, // startTimeMs
                event3StartTime, // endTimeMs: the summary metric endTimeMs is the eventStartTime of each RecordedEvent
                new Attributes());

        List<Metric> expected = List.of(expectedSummary);

        var testClass = new G1GarbageCollectionSummarizer(summaryStartTime);

        when(event1.getStartTime()).thenReturn(Instant.ofEpochMilli(event1StartTime));
        when(event1.getDuration("duration")).thenReturn(Duration.ofNanos(event1DurationNanos));

        when(event2.getStartTime()).thenReturn(Instant.ofEpochMilli(event2StartTime));
        when(event2.getDuration("duration")).thenReturn(Duration.ofNanos(event2DurationNanos));

        when(event3.getStartTime()).thenReturn(Instant.ofEpochMilli(event3StartTime));
        when(event3.getDuration("duration")).thenReturn(Duration.ofNanos(event3DurationNanos));

        // Summarize all events
        testClass.apply(event1);
        testClass.apply(event2);
        testClass.apply(event3);

        final List<Summary> result = testClass.summarizeAndReset().collect(toList());
        final Summary resultSummary = result.get(0);
        final Summary resetResultSummary = testClass.summarizeAndReset().collect(toList()).get(0);

        assertEquals(expected, result);
        assertEquals(numOfEvents, resultSummary.getCount());
        assertEquals(summedDurationMillis, resultSummary.getSum());
        assertEquals(event3DurationMillis, resultSummary.getMin());
        assertEquals(event2DurationMillis, resultSummary.getMax());

        // Summary should be reset to default values
        assertEquals(defaultCount, resetResultSummary.getCount());
        assertEquals(defaultSum, resetResultSummary.getSum());
        assertEquals(defaultMin, resetResultSummary.getMin());
        assertEquals(defaultMax, resetResultSummary.getMax());
    }
}
