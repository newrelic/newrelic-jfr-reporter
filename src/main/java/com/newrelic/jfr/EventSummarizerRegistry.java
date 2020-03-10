package com.newrelic.jfr;

import com.newrelic.jfr.summarizers.ObjectAllocationOutsideTLABSummarizer;
import com.newrelic.jfr.summarizers.ObjectAllocationInNewTLABSummarizer;
import com.newrelic.jfr.summarizers.EventSummarizer;
import com.newrelic.jfr.summarizers.G1GarbageCollectionSummarizer;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public final class EventSummarizerRegistry {

    private final Collection<EventSummarizer> summarizers;

    private EventSummarizerRegistry(Collection<EventSummarizer> summarizers) {
        this.summarizers = summarizers;
    }

    public static EventSummarizerRegistry createDefault() {
        return new EventSummarizerRegistry(List.of(
                new ObjectAllocationInNewTLABSummarizer(),
                new ObjectAllocationOutsideTLABSummarizer(),
                new G1GarbageCollectionSummarizer() // TODO Do we want this summarizer?
        ));
    }

    public Stream<EventSummarizer> stream() {
        return summarizers.stream();
    }
}
