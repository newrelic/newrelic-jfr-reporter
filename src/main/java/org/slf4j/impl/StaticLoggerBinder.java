package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

public class StaticLoggerBinder implements LoggerFactoryBinder {

  private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

  public static final StaticLoggerBinder getSingleton() {
    return SINGLETON;
  }

  public static String REQUESTED_API_VERSION = "1.6.99";

  private static final String loggerFactoryClassStr = NR4jLoggerFactory.class.getName();

  private final ILoggerFactory loggerFactory;

  private StaticLoggerBinder() {
    loggerFactory = new NR4jLoggerFactory();
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

