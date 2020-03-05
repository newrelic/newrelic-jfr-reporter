package org.slf4j.impl;

import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import java.util.logging.Level;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

public class AgentLoggerAdapter extends MarkerIgnoringBase {

  private final Logger logger;

  public AgentLoggerAdapter(String name) {
    this.name = name;
    this.logger = NewRelic.getAgent().getLogger();
  }

  @Override
  public String getName() {
    return name;
  }

  private void formatAndLog(Level level, String format, Object... arguments) {
    FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
    Throwable throwable = tp.getThrowable();
    String message = tp.getMessage();
    if (throwable != null) {
      logger.log(level, throwable, message);
    } else {
      logger.log(level, message);
    }
  }

  @Override
  public boolean isTraceEnabled() {
    return true;
  }

  @Override
  public void trace(String s) {
    formatAndLog(Level.FINEST, s);
  }

  @Override
  public void trace(String s, Object o) {
    formatAndLog(Level.FINEST, s, o);
  }

  @Override
  public void trace(String s, Object o, Object o1) {
    formatAndLog(Level.FINEST, s, o, o1);
  }

  @Override
  public void trace(String s, Object... objects) {
    formatAndLog(Level.FINEST, s, objects);
  }

  @Override
  public void trace(String s, Throwable throwable) {
    formatAndLog(Level.FINEST, s, throwable);
  }

  @Override
  public boolean isDebugEnabled() {
    return true;
  }

  @Override
  public void debug(String s) {
    formatAndLog(Level.FINEST, s);
  }

  @Override
  public void debug(String s, Object o) {
    formatAndLog(Level.FINEST, s, o);
  }

  @Override
  public void debug(String s, Object o, Object o1) {
    formatAndLog(Level.FINEST, s, o, o1);
  }

  @Override
  public void debug(String s, Object... objects) {
    formatAndLog(Level.FINEST, s, objects);
  }

  @Override
  public void debug(String s, Throwable throwable) {
    formatAndLog(Level.FINEST, s, throwable);
  }

  @Override
  public boolean isInfoEnabled() {
    return true;
  }

  @Override
  public void info(String s) {
    formatAndLog(Level.INFO, s);
  }

  @Override
  public void info(String s, Object o) {
    formatAndLog(Level.INFO, s, o);
  }

  @Override
  public void info(String s, Object o, Object o1) {
    formatAndLog(Level.INFO, s, o, o1);
  }

  @Override
  public void info(String s, Object... objects) {
    formatAndLog(Level.INFO, s, objects);
  }

  @Override
  public void info(String s, Throwable throwable) {
    formatAndLog(Level.INFO, s, throwable);
  }

  @Override
  public boolean isWarnEnabled() {
    return true;
  }

  @Override
  public void warn(String s) {
    formatAndLog(Level.WARNING, s);
  }

  @Override
  public void warn(String s, Object o) {
    formatAndLog(Level.WARNING, s, o);
  }

  @Override
  public void warn(String s, Object... objects) {
    formatAndLog(Level.WARNING, s, objects);
  }

  @Override
  public void warn(String s, Object o, Object o1) {
    formatAndLog(Level.WARNING, s, o, o1);
  }

  @Override
  public void warn(String s, Throwable throwable) {
    formatAndLog(Level.WARNING, s, throwable);
  }

  @Override
  public boolean isErrorEnabled() {
    return true;
  }

  @Override
  public void error(String s) {
    formatAndLog(Level.SEVERE, s);
  }

  @Override
  public void error(String s, Object o) {
    formatAndLog(Level.SEVERE, s, o);
  }

  @Override
  public void error(String s, Object o, Object o1) {
    formatAndLog(Level.SEVERE, s, o, o1);
  }

  @Override
  public void error(String s, Object... objects) {
    formatAndLog(Level.SEVERE, s, objects);
  }

  @Override
  public void error(String s, Throwable throwable) {
    formatAndLog(Level.SEVERE, s, throwable);
  }


}
