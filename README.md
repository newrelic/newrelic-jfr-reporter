# JFR Reporter
An agent extension for sending JFR profiling data to New Relic.


## Building

`./gradlew build`

or via grandcentral with

`grandcentral build`

## Maven

The extension requires the JVM to be run with JDK 14 which isn't presently supported by gradle. The `jfr-reporter` dependency can be added from [Artifactory](https://pdx-artifacts.pdx.vm.datanerd.us/repo) via maven.

```    
<dependency>
    <groupId>com.newrelic.agent.java</groupId>
    <artifactId>jfr-reporter</artifactId>
    <version>0.3.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

## Usage

The `jfr-reporter-0.3.0-SNAPSHOT.jar` must be present in the `newrelic/extensions/` directory during agent startup and requires the JVM to be run with JDK 14.

The JFR Reporter can be configured via the agent as follows and requires an [insert API key](https://docs.newrelic.com/docs/apis/get-started/intro-apis/types-new-relic-api-keys#event-insert-key) to send dimensional metrics in to metric ingest API:

```yaml
common:
  jfr:
    enabled: true
    audit_mode: false

  metric_ingest_uri: https://staging-metric-api.newrelic.com
  insert_api_key: 'abc123'
```

#### Audit Mode 

Audit mode will enable audit logging in the Telemetry SDK. The New Relic agent logs will include the serialized json of the reported JFR metric batch.  

Be sure to disable audit logging as soon as you are finished using it. This feature may overload the log file if left turned on for extended periods of time. 
