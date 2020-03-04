package com.newrelic.jfr;

import com.newrelic.jfr.mappers.*;
import com.newrelic.telemetry.metrics.MetricBuffer;

import java.util.Map;
import java.util.function.Supplier;

public class MapperRegistry {

    private final Map<String, JfrStreamEventConsumer> mappers;

    public MapperRegistry(Supplier<MetricBuffer> metricBuffer) {
        mappers = Map.of(
                CPULoadMapper.EVENT_NAME, new JfrStreamEventConsumer(new CPULoadMapper(), metricBuffer),
                GCHeapSummaryMapper.EVENT_NAME, new JfrStreamEventConsumer(new GCHeapSummaryMapper(), metricBuffer),
                GarbageCollectionMapper.EVENT_NAME, new JfrStreamEventConsumer(new GarbageCollectionMapper(), metricBuffer),
                MetaspaceSummaryMapper.EVENT_NAME, new JfrStreamEventConsumer(new MetaspaceSummaryMapper(), metricBuffer),
                ThreadAllocationStatisticsMapper.EVENT_NAME, new JfrStreamEventConsumer(new ThreadAllocationStatisticsMapper(), metricBuffer),
                ObjectAllocationInNewTLABMapper.EVENT_NAME, new JfrStreamEventConsumer(new ObjectAllocationInNewTLABMapper(), metricBuffer),
                ObjectAllocationOutsideTLABMapper.EVENT_NAME, new JfrStreamEventConsumer(new ObjectAllocationOutsideTLABMapper(), metricBuffer),
                AllocationRequiringGCMapper.EVENT_NAME, new JfrStreamEventConsumer(new AllocationRequiringGCMapper(), metricBuffer)
        );
    }

    public Map<String, JfrStreamEventConsumer> getMappers() {
        return mappers;
    }
}
