package com.newrelic.jfr.attributes;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.telemetry.Attributes;

import java.util.function.Supplier;

import static com.newrelic.jfr.attributes.LinkingMetadata.*;

public class CommonAttributes implements Supplier<Attributes> {
    String appName = NewRelic.getAgent().getConfig().getValue("app_name");
    @Override
    public Attributes get() {
        return new Attributes()
                .put("host", getHostname())
                .put(HOSTNAME, getHostname())
                .put("appName", appName)
                .put("service.name", appName)
                .put("instrumentation.name", "JFR")
                .put("instrumentation.provider", "JFR Agent Extension")
                .put("collector.name", "JFR Agent Extension");
    }
}
