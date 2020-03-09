package com.newrelic.jfr;

import com.newrelic.jfr.mappers.EventMapper;
import com.newrelic.jfr.summarizers.AllocationOutsideTLABSummarizer;
import com.newrelic.jfr.summarizers.AllocationTLABSummarizer;
import com.newrelic.jfr.summarizers.EventSummarizer;
import com.newrelic.telemetry.Attributes;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class EventSummarizerRegistry {

    private final Collection<EventSummarizer> summarizers;

    private EventSummarizerRegistry(Collection<EventSummarizer> summarizers) {
        this.summarizers = summarizers;
    }

    public static EventSummarizerRegistry createDefault() {
        Attributes baseAttributes = new Attributes();
        var summarizers = List.of(
                new AllocationTLABSummarizer(baseAttributes),
                new AllocationOutsideTLABSummarizer(baseAttributes)
//                "jdk.G1GarbageCollection", new G1GarbageCollectionSummarizer(baseAttributes)
        );
        return new EventSummarizerRegistry(summarizers);
    }

    public Stream<EventSummarizer> stream() {
        return summarizers.stream();
    }
}
