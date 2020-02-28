package com.newrelic.jfr.attributes;

import com.newrelic.telemetry.Attributes;
import org.junit.jupiter.api.Test;

import static com.newrelic.jfr.attributes.AttributeNames.SPAN_ID;
import static com.newrelic.jfr.attributes.AttributeNames.TRACE_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BaseAttributesTest {

    @Test
    void testGet() {
        Attributes expectedAttributes = new Attributes();
        expectedAttributes.put(TRACE_ID, "");
        expectedAttributes.put(SPAN_ID, "");
        Attributes baseAttributes = new BaseAttributes().get();

        assertEquals(expectedAttributes, baseAttributes);
    }
}
