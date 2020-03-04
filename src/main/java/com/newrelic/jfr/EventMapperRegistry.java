package com.newrelic.jfr;

import com.newrelic.jfr.mappers.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class EventMapperRegistry {

    private final Collection<EventMapper> mappers;

    public static EventMapperRegistry createDefault() {
        return new EventMapperRegistry(List.of(
                new CPULoadMapper(),
                new GCHeapSummaryMapper(),
                new GarbageCollectionMapper(),
                new MetaspaceSummaryMapper()
        ));
    }

    private EventMapperRegistry(Collection<EventMapper> mappers) {
        this.mappers = mappers;
    }

    public Stream<EventMapper> stream() {
        return mappers.stream();
    }
}
