package com.newrelic.jfr.mappers;

import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedThread;

import java.util.Optional;

public class Workarounds {

    /**
     * There are cases where the event has the wrong type inside it
     * for the thread, so calling ev.getThread(name) internally
     * throws a ClassCastException.  We work around it here by just
     * getting the raw value and checking the type.
     */
    static Optional<String> getThreadName(RecordedEvent ev) {
        Object thisField = ev.getValue("eventThread");
        if (thisField instanceof RecordedThread) {
            return Optional.of(((RecordedThread) thisField).getJavaName());
        }
        return Optional.empty();
    }
}
