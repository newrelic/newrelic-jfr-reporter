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
  void testLogWithThrowable() {
    doReturn(tupleWithThrowable).when(spyTestClass).getFormattingTuple(message, zeroObjects);
    testClass.trace(message, throwable);
    verify(mockAgentLogger).log(Level.FINEST, throwable, message);
  }

  @Test
  void isDebugEnabled() {
  }

  @Test
  void debug() {
  }

  @Test
  void testDebug() {
  }

  @Test
  void testDebug1() {
  }

  @Test
  void testDebug2() {
  }

  @Test
  void testDebug3() {
  }

  @Test
  void isInfoEnabled() {
  }

  @Test
  void info() {
  }

  @Test
  void testInfo() {
  }

  @Test
  void testInfo1() {
  }

  @Test
  void testInfo2() {
  }

  @Test
  void testInfo3() {
  }

  @Test
  void isWarnEnabled() {
  }

  @Test
  void warn() {
  }

  @Test
  void testWarn() {
  }

  @Test
  void testWarn1() {
  }

  @Test
  void testWarn2() {
  }

  @Test
  void testWarn3() {
  }

  @Test
  void isErrorEnabled() {
  }

  @Test
  void error() {
  }

  @Test
  void testError() {
  }

  @Test
  void testError1() {
  }

  @Test
  void testError2() {
  }

  @Test
  void testError3() {
  }
}