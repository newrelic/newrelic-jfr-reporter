package com.newrelic.jfr.logger;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class AgentLoggerFactory implements ILoggerFactory {

  private static final String LOGGER_NAME = "com.newrelic.jfr.logger";
  private AgentLoggerAdapter agentLoggerAdapter;

  public AgentLoggerFactory() {
    this.agentLoggerAdapter = new AgentLoggerAdapter(LOGGER_NAME);
  }

  @Override
  public Logger getLogger(String name) {
      return agentLoggerAdapter;
  }
}

