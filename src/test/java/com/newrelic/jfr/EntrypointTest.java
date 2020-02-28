package com.newrelic.jfr;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.newrelic.api.agent.Config;
import org.junit.jupiter.api.Test;

class EntrypointTest {
  @Test
  void JfrDisabledWhenConfigIsFalse() {
    var mockAgentConfig = mock(Config.class);
    when(mockAgentConfig.getValue("jfr.enabled", false)).thenReturn(false);

    assertTrue(Entrypoint.isJfrDisabled(mockAgentConfig));
  }

  @Test
  void JfrEnabledWhenConfigIsTrue() {
    var mockAgentConfig = mock(Config.class);
    when(mockAgentConfig.getValue("jfr.enabled", false)).thenReturn(true);

    assertFalse(Entrypoint.isJfrDisabled(mockAgentConfig));
  }
}