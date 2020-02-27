package com.newrelic.jfr.attributes;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.telemetry.Attributes;

import java.util.function.Supplier;

import static com.newrelic.jfr.attributes.AttributeUtil.*;
import static com.newrelic.jfr.attributes.LinkingMetadata.getHostname;

public class CommonAttributes implements Supplier<Attributes> {
    private String appName = NewRelic.getAgent().getConfig().getValue("app_name");

    @Override
    public Attributes get() {
        final String hostname = getHostname();
        appName = appName != null ? appName : ""; // TODO omit appName if empty?
        return new Attributes()
                .put(HOST, hostname)
                .put(HOSTNAME, hostname)
                .put(APP_NAME, appName)
                .put(SERVICE_NAME, appName)
                .put(INSTRUMENTATION_NAME, "JFR")
                .put(INSTRUMENTATION_PROVIDER, "JFR Agent Extension")
                .put(COLLECTOR_NAME, "JFR Agent Extension");
    }
}
