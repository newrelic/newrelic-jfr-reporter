package com.newrelic.jfr.agent;

import com.newrelic.api.agent.Agent;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;

public class AgentPoller {
    private final Agent agent;
    private final AppNamePoller appNamePoller;
    private final LinkingMetadataPoller linkingMetadataPoller;
    private final ScheduledExecutorService scheduledExecutorService;
    private final long pollPeriodMs;

    public AgentPoller(Agent agent, AppNamePoller appNamePoller, LinkingMetadataPoller linkingMetadataPoller) {
        this(agent, appNamePoller, linkingMetadataPoller, Executors.newSingleThreadScheduledExecutor(), TimeUnit.SECONDS.toMillis(1));
    }

    // Exists only for testing
    AgentPoller(Agent agent, AppNamePoller appNamePoller, LinkingMetadataPoller linkingMetadataPoller, ScheduledExecutorService scheduledExecutorService, long pollPeriodMs) {
        this.agent = agent;
        this.appNamePoller = appNamePoller;
        this.linkingMetadataPoller = linkingMetadataPoller;
        this.scheduledExecutorService = scheduledExecutorService;
        this.pollPeriodMs = pollPeriodMs;
    }

    public static AgentPoller create(Agent agent, Consumer<Map<String,String>> listener){
        return new AgentPoller(agent, new AppNamePoller(agent, listener), new LinkingMetadataPoller(agent, listener));
    }

    public void run() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (appNamePoller.poll() && linkingMetadataPoller.poll()) {
                scheduledExecutorService.shutdown();
                agent.getLogger().log(Level.INFO, "New Relic JFR Monitor finished getting common attributes.");
            }
        }, 0, pollPeriodMs, TimeUnit.MILLISECONDS);
    }
}
