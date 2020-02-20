# JFR Reporter
A library for sending JFR profiling data to New Relic.


## Building

`mvn package`

or via grandcentral with

`grandcentral build`

## Usage

* Gradle

```
compile(group: 'com.newrelic.agent.java', name: 'jfr-reporter', version: '0.1-SNAPSHOT')
```

And then in code:

```java

var config = Config.builder()
    .apiKey(yourApiKey)
    .commonAttributes(yourCommonAttrs)
    .logger(yourLogger)
    .metricsIngestUrl(optionalOverrideUri)
    .build();
var reporter = new Reporter(config);
reporter.start();

```

