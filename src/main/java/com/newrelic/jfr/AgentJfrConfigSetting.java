package com.newrelic.jfr;

import com.newrelic.api.agent.Config;

public class AgentJfrConfigSetting {
  private boolean jfrEnabledSetting;

  public AgentJfrConfigSetting(Config agentConfig) {
    this.jfrEnabledSetting = agentConfig.getValue("jfr.enabled", false);
  }

  public boolean isJfrEnabled(){
    return jfrEnabledSetting;
  }
}
