plugins {
    java
}

repositories {
    mavenCentral()
    maven {
        setUrl("https://dl.bintray.com/mockito/maven/")
    }
}

group = "com.newrelic.agent.library" // TODO Decide on group name.
val releaseVersion: String? by project
version = releaseVersion ?: project.version

dependencies {
    // TODO The agent API is available, remove if it's not needed.
    compileOnly("com.newrelic.agent.java:newrelic-api:+")

    testCompile("com.newrelic.agent.java:newrelic-api:+")
    testCompile("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    testCompile("org.mockito:mockito-core:3.1.1")
}

tasks.test {
    useJUnitPlatform()
}

val jar by tasks.getting(Jar::class) {
    exclude("org/**")

    manifest {
        attributes(mapOf(
                // TODO determine how this library actually works with the agent.
                //  Is there a Pre-Main or Main entry point?
                "Premain-Class" to "com.newrelic.agent.jfr.reporter.TBD"
        ))
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

apply(from = "$rootDir/gradle/publish.gradle.kts")
apply(from = "$rootDir/gradle/versioning.gradle.kts")
