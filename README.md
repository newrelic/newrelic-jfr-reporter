# JFR Reporter

A library for sending JFR profiling data to New Relic.

## Usage
```yaml
TBD
```

## Versioning

This project adheres to the [Semantic Versioning Specification](https://semver.org/spec/v2.0.0.html) of `MAJOR.MINOR.PATCH`.

## Release Process

#### Publish to Staging Repo
// TODO update with correct link when available
The release job can be kicked off on Grand Central:
https://grand-central.datanerd.us/#/projects/3308/builds

// TODO update with correct link when available
And the results can be found here:
https://grand-central-build.pdx.vm.datanerd.us/job/java-agent-jfr-reporter-master/

After the release job has run it will publish the new artifact to a staging repository on Sonatype at https://oss.sonatype.org/#stagingRepositories

#### Release Staging Repo
1. Find the staging repo on Sonatype, which should be named similar to `comnewrelic-nnnn`, and validate that the contents and version look correct.
2. If the contents look correct, select the staging repo and choose `close`, leaving a comment such as `releasing 0.1.0`.
3. When the staging repo is finished closing, select the staging repo and choose `release`, keeping the `Automatically Drop` checkbox checked, and leave a comment such as `releasing 0.1.0`.
4. Verify that the artifact was published on Maven Central at: https://repo1.maven.org/maven2/com/newrelic/agent/library/jfr-reporter/ // TODO update with correct link when available

#### Post Release
* Update the [gradle.properties](gradle.properties) file with a snapshot of the next version to be released (e.g. `version=0.2.0-SNAPSHOT"`).
* Update the [Usage](#usage) example in the [README](README.md) if applicable.
* Edit the new [release](https://source.datanerd.us/java-agent/jfr-reporter/releases) and set the release title to the version (e.g. `v0.1.0`). Also add a `## Release Notes` section at the top with a high level summary of what was improved/fixed with the release.
