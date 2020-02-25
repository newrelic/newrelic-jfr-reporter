package com.newrelic.jfr;

import com.newrelic.api.agent.Logger;
import com.newrelic.telemetry.Attributes;

import java.net.URI;

public class Config {
    public static final URI DEFAULT_METRIC_INGEST_URI = URI.create("https://metric-api.newrelic.com");
    private final Attributes attributes;
    private final String insertApiKey;
    private final URI metricIngestUri;
    private final Logger logger;

    public Config(Builder builder) {
        this.attributes = builder.attributes;
        this.insertApiKey = builder.insertApiKey;
        this.metricIngestUri = builder.metricIngestUri == null ? DEFAULT_METRIC_INGEST_URI : builder.metricIngestUri;
        this.logger = builder.logger;
    }

    public Attributes getCommonAttributes() {
        return attributes;
    }

    public String getInsertApiKey() {
        return insertApiKey;
    }

    public URI getMetricIngestUri() {
        return metricIngestUri;
    }

    public Logger getLogger() {
        return logger;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Attributes attributes;
        private String insertApiKey;
        private URI metricIngestUri;
        private Logger logger;

        public Builder commonAttributes(Attributes attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder insertApiKey(String insertApiKey) {
            this.insertApiKey = insertApiKey;
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

        public Config build() {
            return new Config(this);
        }
    }
}
