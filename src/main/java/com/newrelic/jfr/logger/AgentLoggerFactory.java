package com.newrelic.jfr.logger;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class AgentLoggerFactory implements ILoggerFactory {

  private static final String LOGGER_NAME = "com.newrelic.jfr.logger";
  private AgentLoggerAdapter agentLoggerAdapter;

  public AgentLoggerFactory(com.newrelic.api.agent.Logger agentLogger) {
    this.agentLoggerAdapter = new AgentLoggerAdapter(LOGGER_NAME, agentLogger);
  }

  @Override
  public Logger getLogger(String name) {
      return agentLoggerAdapter;
  }
}

