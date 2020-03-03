package com.newrelic.jfr.agent;

import com.newrelic.api.agent.Agent;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AgentPollerTest {

    @Test
    void testPolling() throws Exception {
        var agent = mock(Agent.class);
        var appNamePoller = mock(AppNamePoller.class);
        var metadataPoller = mock(LinkingMetadataPoller.class);

        when(appNamePoller.poll()).thenReturn(false, true);
        when(metadataPoller.poll()).thenReturn(false, false, true);

        var executorService = Executors.newSingleThreadScheduledExecutor();
        var testClass = new AgentPoller(agent, appNamePoller, metadataPoller, executorService, 1);
        testClass.run();
        assertTrue(executorService.awaitTermination(1, TimeUnit.SECONDS));
    }

}