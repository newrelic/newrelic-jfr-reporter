package com.newrelic.jfr.mappers;

import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedThread;

import java.util.Optional;

public class Workarounds {

    static Optional<String> getThreadName(RecordedEvent ev) {
        Object thisField = ev.getValue("eventThread");
        if (thisField instanceof RecordedThread) {
            return Optional.of(((RecordedThread) thisField).getJavaName());
        }
        return Optional.empty();
    }
}
