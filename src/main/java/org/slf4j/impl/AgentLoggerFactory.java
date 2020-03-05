package org.slf4j.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class AgentLoggerFactory implements ILoggerFactory {

  ConcurrentMap<String, Logger> loggerMap;

  AgentLoggerFactory() {
    loggerMap = new ConcurrentHashMap<String, Logger>();
  }

  @Override
  public Logger getLogger(String name) {
    Logger agentLogger = loggerMap.get(name);
    if (agentLogger != null) {
      return agentLogger;
    } else {
      Logger newInstance = new AgentLoggerAdapter(name);
      Logger oldInstance = loggerMap.putIfAbsent(name, newInstance);
      return oldInstance == null ? newInstance : oldInstance;
    }
  }
}
