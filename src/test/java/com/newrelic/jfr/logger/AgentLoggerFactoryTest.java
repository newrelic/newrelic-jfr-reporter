/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.newrelic.jfr.logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

class AgentLoggerFactoryTest {

  @Test
  void GetAgentLoggerAdapter() {
    com.newrelic.api.agent.Logger agentLogger = mock(com.newrelic.api.agent.Logger.class);
    AgentLoggerFactory testClass = new AgentLoggerFactory(agentLogger);
    Logger logger = testClass.getLogger("should not be this name");
    assertEquals(logger.getName(), "com.newrelic.jfr.logger");
  }
}