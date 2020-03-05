package com.newrelic.jfr.logger;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class AgentLoggerFactory implements ILoggerFactory {

  private static final String LOGGER_NAME = "Agent_SLF4J_Custom_Logger";
  private AgentLoggerAdapter agentLoggerAdapter;

  public AgentLoggerFactory() {
  }

  @Override
  public Logger getLogger(String name) {
    if (agentLoggerAdapter != null) {
      return agentLoggerAdapter;
    }
    return new AgentLoggerAdapter(LOGGER_NAME);
  }
}

