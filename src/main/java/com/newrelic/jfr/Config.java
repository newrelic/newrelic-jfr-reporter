package com.newrelic.jfr;

import com.newrelic.api.agent.Logger;
import com.newrelic.telemetry.Attributes;

import java.net.URI;

public class Config {

  public static final URI DEFAULT_METRIC_INGEST_URI = URI.create("https://metric-api.newrelic.com");
  public static final URI DEFAULT_EVENT_INGEST_URI = URI.create("https://metric-api.newrelic.com");
  public static final String METRIC_INGEST_URI = "metric_ingest_uri";
  public static final String INSERT_API_KEY = "insert_api_key";
  public static final String JFR_ENABLED = "jfr.enabled";
  public static final String JFR_AUDIT_MODE = "jfr.audit_mode";
  private final Attributes commonAttributes;
  private final String insertApiKey;
  private final URI metricIngestUri;
  private final URI eventIngestUri;
  private final Logger logger;
  private final ToMetricRegistry toMetricRegistry;
  private final ToSummaryRegistry toSummaryRegistry;
  private final boolean auditMode;

  public Config(Builder builder) {
    this.commonAttributes = builder.commonAttributes;
    this.insertApiKey = builder.insertApiKey;
    this.metricIngestUri = builder.metricIngestUri;
    this.eventIngestUri = builder.eventIngestUri;
    this.logger = builder.logger;
    this.toMetricRegistry = builder.mapperRegistry;
    this.toSummaryRegistry = builder.summarizerRegistry;
    this.auditMode = builder.auditMode;
  }

  public Attributes getCommonAttributes() {
    return commonAttributes;
  }

  public String getInsertApiKey() {
    return insertApiKey;
  }

  public URI getMetricIngestUri() {
    return metricIngestUri;
  }

  public URI getEventIngestUri() {
    return eventIngestUri;
  }

  public Logger getLogger() {
    return logger;
  }

  public ToMetricRegistry getToMetricRegistry() {
    return toMetricRegistry;
  }

  public ToSummaryRegistry getToSummaryRegistry() {
    return toSummaryRegistry;
  }

  public boolean isAuditMode() {
    return auditMode;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {


    private Attributes commonAttributes;
    private String insertApiKey;
    private URI metricIngestUri = DEFAULT_METRIC_INGEST_URI;
    private URI eventIngestUri = DEFAULT_EVENT_INGEST_URI;
    private Logger logger;
    private ToMetricRegistry mapperRegistry;
    private ToSummaryRegistry summarizerRegistry;
    private boolean auditMode;

    public Builder commonAttributes(Attributes commonAttributes) {
      this.commonAttributes = commonAttributes;
      return this;
    }

    public Builder insertApiKey(String insertApiKey) {
      this.insertApiKey = insertApiKey;
      return this;
    }

    public Builder metricsIngestUri(String metricIngestUri) {
      if ((metricIngestUri != null) && (!metricIngestUri.isEmpty())) {
        return metricsIngestUri(URI.create(metricIngestUri));
      }
      return this;
    }

    public Builder metricsIngestUri(URI metricIngestUri) {
      this.metricIngestUri = metricIngestUri;
      return this;
    }

    public Builder logger(Logger logger) {
      this.logger = logger;
      return this;
    }

    public Builder toMetricRegistry(ToMetricRegistry mapperRegistry) {
      this.mapperRegistry = mapperRegistry;
      return this;
    }

    public Builder summarizerRegistry(ToSummaryRegistry summarizerRegistry) {
      this.summarizerRegistry = summarizerRegistry;
      return this;
    }

    public Builder auditMode(boolean jfrAuditMode) {
      this.auditMode = jfrAuditMode;
      return this;
    }

    public Config build() {
      return new Config(this);
    }

  }
}
