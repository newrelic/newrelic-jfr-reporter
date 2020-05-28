package com.newrelic.jfr;

import com.newrelic.jfr.mappers.CPULoadMapper;
import com.newrelic.jfr.mappers.EventMapper;
import com.newrelic.jfr.mappers.GCHeapSummaryMapper;
import com.newrelic.jfr.mappers.GarbageCollectionMapper;
import com.newrelic.jfr.mappers.MetaspaceSummaryMapper;
import com.newrelic.jfr.mappers.ThreadAllocationStatisticsMapper;
import com.newrelic.jfr.tometric.AllocationRequiringGCMapper;

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
                new MetaspaceSummaryMapper(),
                new AllocationRequiringGCMapper(),
                new ThreadAllocationStatisticsMapper()
        ));
    }

    private EventMapperRegistry(Collection<EventMapper> mappers) {
        this.mappers = mappers;
    }

    public Stream<EventMapper> stream() {
        return mappers.stream();
    }
}
