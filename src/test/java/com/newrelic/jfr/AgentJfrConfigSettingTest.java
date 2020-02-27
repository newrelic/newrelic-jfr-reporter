package com.newrelic.jfr;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.newrelic.api.agent.Config;
import org.junit.jupiter.api.Test;

class AgentJfrConfigSettingTest {
  @Test
  void JfrRespectsAgentFalseConfigSetting() {
    var mockAgentConfig = mock(Config.class);
    when(mockAgentConfig.getValue("jfr.enabled", false)).thenReturn(false);
    var testClass = new AgentJfrConfigSetting(mockAgentConfig);

    boolean jfrConfigSetting = testClass.isJfrEnabled();

    assertFalse(jfrConfigSetting);
  }
}