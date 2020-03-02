package com.newrelic.jfr;

import com.newrelic.jfr.mappers.CPULoadMapper;
import com.newrelic.jfr.mappers.GCHeapSummaryMapper;
import com.newrelic.jfr.mappers.GarbageCollectionMapper;
import com.newrelic.telemetry.metrics.MetricBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MapperRegistry {

    private final Map<String, JfrStreamEventConsumer> mappers;

    public MapperRegistry(Supplier<MetricBuffer> metricBuffer) {
        mappers = new HashMap<>();
        mappers.put(CPULoadMapper.EVENT_NAME, new JfrStreamEventConsumer(new CPULoadMapper(), metricBuffer));
        mappers.put(GCHeapSummaryMapper.EVENT_NAME, new JfrStreamEventConsumer(new GCHeapSummaryMapper(), metricBuffer));
        mappers.put(GarbageCollectionMapper.EVENT_NAME, new JfrStreamEventConsumer(new GarbageCollectionMapper(), metricBuffer));
    }

    public Map<String, JfrStreamEventConsumer> getMappers() {
        return mappers;
    }
}
