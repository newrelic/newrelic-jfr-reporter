package com.newrelic.jfr.summarizers;

import com.newrelic.telemetry.metrics.Summary;
import jdk.jfr.consumer.RecordingFile;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Comparator;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileBasedSummarizerTest {

    @Test
    public void test_summarys() throws URISyntaxException, IOException {
        var summarizer = new G1GarbageCollectionSummarizer();
        // This is a 1-hr recording file of a not-particularly-high-allocating service
        var recordingFile = new RecordingFile(Paths.get(ClassLoader.getSystemResource("hotspot-pid-213-2019_12_10_17_34_33.jfr").toURI()));
        while (recordingFile.hasMoreEvents()) {
            var event = recordingFile.readEvent();
            assertNotNull(event);
            if (event.getEventType().getName().startsWith("jdk.G1GarbageCollection")) {
                summarizer.apply(event);
            }
        }
        var summary = summarizer.summarizeAndReset().get(0);
        // Numbers confirmed from JMC
        assertEquals(84, summary.getCount(), "84 GCs expected");
        assertEquals(481.0, summary.getSum(), 0.001, "481.0 total pause time expected");
    }

    @Test
    public void test_allocs() throws URISyntaxException, IOException {
        // Test all allocs
        var everythingSummarizer = new PerThreadObjectAllocationInNewTLABSummarizer("everything");
        var allocsByThread = new ObjectAllocationInNewTLABSummarizer();
        var recordingFile = new RecordingFile(Paths.get(ClassLoader.getSystemResource("hotspot-pid-213-2019_12_10_17_34_33.jfr").toURI()));
        while (recordingFile.hasMoreEvents()) {
            var event = recordingFile.readEvent();
            assertNotNull(event);
            if (event.getEventType().getName().startsWith("jdk.ObjectAllocationInNewTLAB")) {
                everythingSummarizer.apply(event);
                allocsByThread.apply(event);
            }
        }

        var sizeComparer = new Comparator<Summary>() {
            @Override
            public int compare(Summary o1, Summary o2) {
                if (o1.getSum() == o2.getSum()) {
                    return 0;
                }
                if (o1.getSum() > o2.getSum()) {
                    return -1;
                }
                return 1;
            }
            // (o1, o2) -> o1.getSum() > o2.getSum() ? -1 : 1
        };


        var summary = everythingSummarizer.summarizeAndReset().get(0);
        assertEquals(67559, summary.getCount(), "67559 allocations expected in file");
        var individualSummaries = allocsByThread.summarizeAndReset();
        assertEquals(181, individualSummaries.size(), "181 threads actually allocated");
        var highAllocs = individualSummaries.stream().filter(s -> s.getCount() > 60).collect(toList());
        assertEquals(34, highAllocs.size(), "34 threads actually allocated more than 60 times in file");
        var top10 = individualSummaries.stream().sorted(sizeComparer).limit(10).collect(toList());
        assertEquals(10, top10.size(), "We have at least 10 allocating threads");
        assertEquals("KafkaConsumerAutoService", top10.get(0).getAttributes().get("threadName"), "The Kafka thread is the hot allocator");
    }
}
