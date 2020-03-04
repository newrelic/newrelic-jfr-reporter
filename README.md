# JFR Reporter
An agent extension for sending JFR profiling data to New Relic.


## Building

`mvn package`

or via grandcentral with

`grandcentral build`

## Maven

The extension requires the JVM to be run with JDK 14 which isn't presently supported by gradle. The `jfr-reporter` dependecny can be added from [Artifactory](https://pdx-artifacts.pdx.vm.datanerd.us/repo) via maven.

```    
<dependency>
    <groupId>com.newrelic.agent.java</groupId>
    <artifactId>jfr-reporter</artifactId>
    <version>0.1.1-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

## Usage

The `jfr-reporter-0.1.1-SNAPSHOT.jar` must be present in the `newrelic/extensions/` directory during agent startup.

The JFR Reporter can be configured via the agent as follows and requires an [insert API key](https://docs.newrelic.com/docs/apis/get-started/intro-apis/types-new-relic-api-keys#event-insert-key) to send dimensional metrics in to metric ingest API:

```yaml
common:
  jfr:
    enabled: true

  metric_ingest_uri: https://staging-metric-api.newrelic.com
  insert_api_key: 'abc123'
```

