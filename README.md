# JFR Reporter
A library for sending JFR profiling data to New Relic.


## Building

`mvn package`

or via grandcentral with

`grandcentral build`

## Usage
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

