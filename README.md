[![Community Project header](https://github.com/newrelic/opensource-website/raw/master/src/images/categories/Community_Project.png)](https://opensource.newrelic.com/oss-category/#community-project)

# newrelic-jfr-reporter 

An agent extension for sending JFR profiling data to New Relic.

## Getting Started

This extension leverages the streaming JFR event APIs, and so 
Java 14+ is required to build and run this agent extension.

## Installation / Usage

The `jfr-reporter-<version>.jar` must be present in the `newrelic/extensions/` directory during agent startup 
and requires the JVM to be run with JDK 14.

The JFR Reporter can be configured via the agent as follows and requires 
an [insert API key](https://docs.newrelic.com/docs/apis/get-started/intro-apis/types-new-relic-api-keys#event-insert-key) to send dimensional metrics in to metric ingest API:

```yaml
common:
  jfr:
    enabled: true
    audit_mode: false

  metric_ingest_uri: https://metric-api.newrelic.com/metric/v1
  insert_api_key: 'abc123'
```

For reporting to the EU region, replace `metric_ingest_uri`:

```yaml
  metric_ingest_uri: https://metric-api.eu.newrelic.com/metric/v1
```

#### Audit Mode 

Audit mode will enable audit logging in the Telemetry SDK. The New Relic agent logs will include the serialized json of the reported JFR metric batch.  

Be sure to disable audit logging as soon as you are finished using it. This feature may overload the log file if left turned on for extended periods of time. 

#### More

For additional installation and configuration of the JFR reporter extension, please see
[the official documentation.](https://docs.newrelic.com/docs/agents/java-agent/features/real-time-java-profiling-using-jfr-metrics#installation)

## Building

`./gradlew build`

## Testing

`./gradlew test`

## Support

New Relic hosts and moderates an online forum where customers can interact with New Relic employees as well as other customers to get help and share best practices. Like all official New Relic open source projects, may be a related Community topic in 
the [New Relic Explorers Hub](https://discuss.newrelic.com/).

## Contributing
We encourage your contributions to improve newrelic-jfr-reporter Keep in mind when you submit your pull request, you'll need to sign the CLA via the click-through using CLA-Assistant. You only have to sign the CLA one time per project.
If you have any questions, or to execute our corporate CLA, required if your contribution is on behalf of a company,  please drop us an email at opensource@newrelic.com.

## License
`newrelic-jfr-reporter` is licensed under the [Apache 2.0](http://apache.org/licenses/LICENSE-2.0.txt) License.

`newrelic-jfr-reporter` also uses source code from third-party libraries. You can find 
full details on which libraries are used and the terms under which they are licensed in 
the third-party notices document.
