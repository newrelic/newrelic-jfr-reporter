package com.newrelic.jfr.agent;

import com.newrelic.api.agent.Agent;
import com.newrelic.telemetry.Attributes;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.newrelic.jfr.attributes.AttributeNames.APP_NAME;
import static com.newrelic.jfr.attributes.AttributeNames.SERVICE_NAME;

public class AppNamePoller {
    private final AtomicBoolean gotAppName = new AtomicBoolean(false);
    private final Agent agent;
    private final Consumer<Attributes> attributesListener;

    public AppNamePoller(Agent agent, Consumer<Attributes> attributesListener) {
        this.agent = agent;
        this.attributesListener = attributesListener;
    }

    boolean poll() {
        if (gotAppName.get()) {
            return true;
        }
        String appName = agent.getConfig().getValue("app_name");
        if (appName != null && !appName.isEmpty()) {
            gotAppName.set(true);
            attributesListener.accept(new Attributes().put(SERVICE_NAME, appName).put(APP_NAME, appName));
            return true;
        }
        return false;
    }
}
