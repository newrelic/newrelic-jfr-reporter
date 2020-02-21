package com.newrelic.jfr;

import com.newrelic.telemetry.Attributes;

import java.net.URI;
import java.util.logging.Logger;

public class Config {

  private static final URI DEFAULT_INGEST_URI = URI.create("https://metric-api.newrelic.com");
  private final Attributes attributes;
  private final String apiKey;
  private final URI metricsIngestUri;
  private final Logger logger;

  public Config(Builder builder) {
    this.attributes = builder.attributes;
    this.apiKey = builder.apiKey;
    this.metricsIngestUri =
        builder.metricsIngestUri == null ? DEFAULT_INGEST_URI : builder.metricsIngestUri;
    this.logger = builder.logger;
  }

  public Attributes getCommonAttributes() {
    return attributes;
  }

  public String getApiKey() {
    return apiKey;
  }

  public URI getMetricsIngestUri() {
    return metricsIngestUri;
  }

  public Logger getLogger() {
    return logger;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private Attributes attributes;
    private String apiKey;
    private URI metricsIngestUri;
    private Logger logger;

    public Builder commonAttributes(Attributes attributes) {
      this.attributes = attributes;
      return this;
    }

    public Builder apiKey(String apiKey) {
      this.apiKey = apiKey;
      return this;
    }

    public Builder metricsIngestUrl(URI metricsIngestUrl) {
      this.metricsIngestUri = metricsIngestUrl;
      return this;
    }

    public Builder logger(Logger logger) {
      this.logger = logger;
      return this;
    }

    public Config build(){
      return new Config(this);
    }
  }
}
