package org.slf4j.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.newrelic.jfr.logger.AgentLoggerFactory;
import org.junit.jupiter.api.Test;

public class StaticLoggerBinderTest {

  @Test
  void GetAgentLoggerFactory() {
    StaticLoggerBinder testClass = StaticLoggerBinder.getSingleton();
    assertTrue(testClass.getLoggerFactory() instanceof AgentLoggerFactory);
  }

}
