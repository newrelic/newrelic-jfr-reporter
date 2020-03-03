package com.newrelic.jfr.agent;

import com.newrelic.api.agent.Agent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class AgentPoller {
    private final Agent agent;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final AppNamePoller appNamePoller;
    private final LinkingMetadataPoller linkingMetadataPoller;

    public AgentPoller(Agent agent, AppNamePoller appNamePoller, LinkingMetadataPoller linkingMetadataPoller) {
        this.agent = agent;
        this.appNamePoller = appNamePoller;
        this.linkingMetadataPoller = linkingMetadataPoller;
    }

    public void run() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (appNamePoller.poll() && linkingMetadataPoller.poll()) {
                scheduledExecutorService.shutdown();
                agent.getLogger().log(Level.INFO, "New Relic JFR Monitor finished getting common attributes.");
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}
