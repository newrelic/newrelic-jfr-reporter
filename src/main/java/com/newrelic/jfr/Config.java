package com.newrelic.jfr;

import com.newrelic.api.agent.Logger;
import com.newrelic.telemetry.Attributes;

import java.net.URI;

public class Config {
    public static final URI DEFAULT_METRIC_INGEST_URI = URI.create("https://metric-api.newrelic.com");
    public static final String METRIC_INGEST_URI = "metric_ingest_uri";
    public static final String INSERT_API_KEY = "insert_api_key";
    private final Attributes commonAttributes;
    private final String insertApiKey;
    private final URI metricIngestUri;
    private final Logger logger;

    public Config(Builder builder) {
        this.commonAttributes = builder.commonAttributes;
        this.insertApiKey = builder.insertApiKey;
        this.metricIngestUri = builder.metricIngestUri == null ? DEFAULT_METRIC_INGEST_URI : builder.metricIngestUri;
        this.logger = builder.logger;
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

    public Logger getLogger() {
        return logger;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Attributes commonAttributes;
        private String insertApiKey;
        private URI metricIngestUri;
        private Logger logger;

        public Builder commonAttributes(Attributes commonAttributes) {
            this.commonAttributes = commonAttributes;
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
