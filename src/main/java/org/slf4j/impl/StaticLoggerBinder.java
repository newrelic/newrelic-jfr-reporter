/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.slf4j.impl;

import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.jfr.logger.AgentLoggerFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

public class StaticLoggerBinder implements LoggerFactoryBinder {

  private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

  public static StaticLoggerBinder getSingleton() {
    return SINGLETON;
  }

  public static String REQUESTED_API_VERSION = "1.6.99";

  private static final String loggerFactoryClassStr = AgentLoggerFactory.class.getName();

  private final ILoggerFactory loggerFactory;

  private StaticLoggerBinder() {
    Logger agentLogger = NewRelic.getAgent().getLogger();
    loggerFactory = new AgentLoggerFactory(agentLogger);
  }

  @Override
  public ILoggerFactory getLoggerFactory() {
    return loggerFactory;
  }

  @Override
  public String getLoggerFactoryClassStr() {
    return loggerFactoryClassStr;
  }
}

