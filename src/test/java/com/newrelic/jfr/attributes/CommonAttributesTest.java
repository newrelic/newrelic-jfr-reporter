package com.newrelic.jfr.attributes;

import com.newrelic.telemetry.Attributes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.newrelic.jfr.attributes.AttributeUtil.*;

class CommonAttributesTest {

    @Test
    void testGet() {
        Attributes expectedAttributes = new Attributes();
        expectedAttributes.put(APP_NAME, "");
        expectedAttributes.put(HOSTNAME, "");
        expectedAttributes.put(HOST, "");
        expectedAttributes.put(SERVICE_NAME, "");
        expectedAttributes.put(COLLECTOR_NAME, "JFR Agent Extension");
        expectedAttributes.put(INSTRUMENTATION_NAME, "JFR");
        expectedAttributes.put(INSTRUMENTATION_PROVIDER, "JFR Agent Extension");

        Attributes commonAttributes = new CommonAttributes().get();
        Assertions.assertEquals(expectedAttributes, commonAttributes);
    }
}