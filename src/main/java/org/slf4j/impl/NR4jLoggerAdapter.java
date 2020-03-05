package org.slf4j.impl;

import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import java.util.logging.Level;
import org.slf4j.helpers.MarkerIgnoringBase;

public class NR4jLoggerAdapter extends MarkerIgnoringBase {

  private final Logger logger;

  public NR4jLoggerAdapter(String name) {
    this.name = name;
    this.logger = NewRelic.getAgent().getLogger();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean isTraceEnabled() {
    return false;
  }

  @Override
  public void trace(String s) {
    logger.log(Level.FINEST, s);
  }

  @Override
  public void trace(String s, Object o) {
    logger.log(Level.FINEST, s, o);
  }

  @Override
  public void trace(String s, Object o, Object o1) {
    logger.log(Level.FINEST, s, o, o1);
  }

  @Override
  public void trace(String s, Object... objects) {
    logger.log(Level.FINEST, s, objects);
  }

  @Override
  public void trace(String s, Throwable throwable) {
    logger.log(Level.FINEST, s, throwable);
  }

  @Override
  public boolean isDebugEnabled() {
    return false;
  }

  @Override
  public void debug(String s) {
    logger.log(Level.FINEST, s);
  }

  @Override
  public void debug(String s, Object o) {
    logger.log(Level.FINEST, s, o);
  }

  @Override
  public void debug(String s, Object o, Object o1) {
    logger.log(Level.FINEST, s, o, o1);
  }

  @Override
  public void debug(String s, Object... objects) {
    logger.log(Level.FINEST, s, objects);
  }

  @Override
  public void debug(String s, Throwable throwable) {
    logger.log(Level.FINEST, s, throwable);
  }

  @Override
  public boolean isInfoEnabled() {
    return false;
  }

  @Override
  public void info(String s) {
    logger.log(Level.INFO, s);
  }

  @Override
  public void info(String s, Object o) {
    logger.log(Level.INFO, s, o);
  }

  @Override
  public void info(String s, Object o, Object o1) {
    logger.log(Level.INFO, s, o, o1);
  }

  @Override
  public void info(String s, Object... objects) {
    logger.log(Level.INFO, s, objects);
  }

  @Override
  public void info(String s, Throwable throwable) {
    logger.log(Level.INFO, s, throwable);
  }

  @Override
  public boolean isWarnEnabled() {
    return false;
  }

  @Override
  public void warn(String s) {
    logger.log(Level.WARNING, s);
  }

  @Override
  public void warn(String s, Object o) {
    logger.log(Level.WARNING, s, o);
  }

  @Override
  public void warn(String s, Object... objects) {
    logger.log(Level.WARNING, s, objects);
  }

  @Override
  public void warn(String s, Object o, Object o1) {
    logger.log(Level.WARNING, s, o, o1);
  }

  @Override
  public void warn(String s, Throwable throwable) {
    logger.log(Level.WARNING, s, throwable);
  }

  @Override
  public boolean isErrorEnabled() {
    return false;
  }

  @Override
  public void error(String s) {
    logger.log(Level.SEVERE, s);
  }

  @Override
  public void error(String s, Object o) {
    logger.log(Level.SEVERE, s, o);
  }

  @Override
  public void error(String s, Object o, Object o1) {
    logger.log(Level.SEVERE, s, o, o1 );
  }

  @Override
  public void error(String s, Object... objects) {
    logger.log(Level.SEVERE, s, objects);
  }

  @Override
  public void error(String s, Throwable throwable) {
    logger.log(Level.SEVERE, s, throwable);
  }
}
