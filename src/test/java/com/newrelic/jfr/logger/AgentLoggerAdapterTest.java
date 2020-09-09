/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.newrelic.jfr.logger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.newrelic.api.agent.Logger;
import java.util.logging.Level;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.FormattingTuple;

class AgentLoggerAdapterTest {

  private Logger mockAgentLogger = mock(Logger.class);
  private AgentLoggerAdapter testClass = new AgentLoggerAdapter("test logger", mockAgentLogger);
  private AgentLoggerAdapter spyTestClass = spy(testClass);
  private String message = "freezing sizzle";
  private Object[] zeroObjects = new Object[0];
  private Object[] oneObject = {new Object()};
  private Object[] twoObjects = {new Object(), new Object()};
  private Throwable throwable = new Throwable();
  private FormattingTuple tupleNoThrowable = new FormattingTuple(message, null, null);
  private FormattingTuple tupleWithThrowable = new FormattingTuple(message, null, throwable);

  @Test
  void isTraceEnabled() {
    assertTrue(testClass.isTraceEnabled());
  }

  @Test
  void traceLogWithString() {
    doReturn(tupleNoThrowable).when(spyTestClass).getFormattingTuple(message, zeroObjects);
    testClass.trace(message);
    verify(mockAgentLogger).log(Level.FINEST, message);
  }

  @Test
  void traceLogWithStringAndObject() {
    doReturn(tupleNoThrowable).when(spyTestClass).getFormattingTuple(message, oneObject);
    testClass.trace(message, oneObject);
    verify(mockAgentLogger).log(Level.FINEST, message);
  }

  @Test
  void traceLogWithStringAndTwoObjects() {
    doReturn(tupleNoThrowable).when(spyTestClass).getFormattingTuple(message, twoObjects);
    testClass.trace(message, twoObjects);
    verify(mockAgentLogger).log(Level.FINEST, message);
  }

  @Test
  void traceLogWithThrowable() {
    doReturn(tupleWithThrowable).when(spyTestClass).getFormattingTuple(message, zeroObjects);
    testClass.trace(message, throwable);
    verify(mockAgentLogger).log(Level.FINEST, throwable, message);
  }

  @Test
  void isDebugEnabled() {
    assertTrue(testClass.isDebugEnabled());
  }

  @Test
  void debugLogWithString() {
    doReturn(tupleNoThrowable).when(spyTestClass).getFormattingTuple(message, zeroObjects);
    testClass.debug(message);
    verify(mockAgentLogger).log(Level.FINEST, message);
  }

  @Test
  void debugLogWithStringAndObject() {
    doReturn(tupleNoThrowable).when(spyTestClass).getFormattingTuple(message, oneObject);
    testClass.debug(message, oneObject);
    verify(mockAgentLogger).log(Level.FINEST, message);
  }

  @Test
  void debugLogWithStringAndTwoObjects() {
    doReturn(tupleNoThrowable).when(spyTestClass).getFormattingTuple(message, twoObjects);
    testClass.debug(message, twoObjects);
    verify(mockAgentLogger).log(Level.FINEST, message);
  }

  @Test
  void debugLogWithThrowable() {
    doReturn(tupleWithThrowable).when(spyTestClass).getFormattingTuple(message, zeroObjects);
    testClass.debug(message, throwable);
    verify(mockAgentLogger).log(Level.FINEST, throwable, message);
  }

  @Test
  void isInfoEnabled() {
    assertTrue(testClass.isInfoEnabled());
  }

  @Test
  void infoLogWithString() {
    doReturn(tupleNoThrowable).when(spyTestClass).getFormattingTuple(message, zeroObjects);
    testClass.info(message);
    verify(mockAgentLogger).log(Level.INFO, message);
  }

  @Test
  void infoLogWithStringAndObject() {
    doReturn(tupleNoThrowable).when(spyTestClass).getFormattingTuple(message, oneObject);
    testClass.info(message, oneObject);
    verify(mockAgentLogger).log(Level.INFO, message);
  }

  @Test
  void infoLogWithStringAndTwoObjects() {
    doReturn(tupleNoThrowable).when(spyTestClass).getFormattingTuple(message, twoObjects);
    testClass.info(message, twoObjects);
    verify(mockAgentLogger).log(Level.INFO, message);
  }

  @Test
  void infoLogWithThrowable() {
    doReturn(tupleWithThrowable).when(spyTestClass).getFormattingTuple(message, zeroObjects);
    testClass.info(message, throwable);
    verify(mockAgentLogger).log(Level.INFO, throwable, message);
  }

  @Test
  void isWarnEnabled() {
    assertTrue(testClass.isWarnEnabled());
  }

  @Test
  void warnLogWithString() {
    doReturn(tupleNoThrowable).when(spyTestClass).getFormattingTuple(message, zeroObjects);
    testClass.warn(message);
    verify(mockAgentLogger).log(Level.WARNING, message);
  }

  @Test
  void warnLogWithStringAndObject() {
    doReturn(tupleNoThrowable).when(spyTestClass).getFormattingTuple(message, oneObject);
    testClass.warn(message, oneObject);
    verify(mockAgentLogger).log(Level.WARNING, message);
  }

  @Test
  void warnLogWithStringAndTwoObjects() {
    doReturn(tupleNoThrowable).when(spyTestClass).getFormattingTuple(message, twoObjects);
    testClass.warn(message, twoObjects);
    verify(mockAgentLogger).log(Level.WARNING, message);
  }

  @Test
  void warnLogWithThrowable() {
    doReturn(tupleWithThrowable).when(spyTestClass).getFormattingTuple(message, zeroObjects);
    testClass.warn(message, throwable);
    verify(mockAgentLogger).log(Level.WARNING, throwable, message);
  }

  @Test
  void isErrorEnabled() {
    assertTrue(testClass.isErrorEnabled());
  }

  @Test
  void errorLogWithString() {
    doReturn(tupleNoThrowable).when(spyTestClass).getFormattingTuple(message, zeroObjects);
    testClass.error(message);
    verify(mockAgentLogger).log(Level.SEVERE, message);
  }

  @Test
  void errorLogWithStringAndObject() {
    doReturn(tupleNoThrowable).when(spyTestClass).getFormattingTuple(message, oneObject);
    testClass.error(message, oneObject);
    verify(mockAgentLogger).log(Level.SEVERE, message);
  }

  @Test
  void errorLogWithStringAndTwoObjects() {
    doReturn(tupleNoThrowable).when(spyTestClass).getFormattingTuple(message, twoObjects);
    testClass.error(message, twoObjects);
    verify(mockAgentLogger).log(Level.SEVERE, message);
  }

  @Test
  void errorLogWithThrowable() {
    doReturn(tupleWithThrowable).when(spyTestClass).getFormattingTuple(message, zeroObjects);
    testClass.error(message, throwable);
    verify(mockAgentLogger).log(Level.SEVERE, throwable, message);
  }

}