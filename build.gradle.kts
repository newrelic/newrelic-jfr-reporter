buildscript {
    repositories {
        mavenCentral()
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "6.0.0"
    `maven-publish`
    signing
}

version = "0.2.0"
group = "com.newrelic.agent.extension"

java {
    sourceCompatibility = JavaVersion.VERSION_14
    targetCompatibility = JavaVersion.VERSION_14
    disableAutoTargetJvm()
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    api("com.newrelic.agent.java:newrelic-api:5.13.0")
    implementation("com.newrelic.telemetry:telemetry:0.6.1")
    implementation("com.newrelic.telemetry:telemetry-http-okhttp:0.6.1")
    implementation("com.newrelic:jfr-mappers:0.3.0")
    implementation("org.slf4j:slf4j-api:1.7.26")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testImplementation("org.mockito:mockito-junit-jupiter:3.3.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.named("build") { dependsOn("shadowJar") }
tasks.shadowJar {
    archiveClassifier.set("")
    manifest {
        attributes(
                "Premain-Class" to "com.newrelic.jfr.Entrypoint",
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to "New Relic, Inc.",
                "Can-Redefine-Classes" to false,
                "Can-Retransform-Classes" to false
        )
    }
    relocate("okio", "com.newrelic.deps.jfr.okio")
    relocate("okhttp3", "com.newrelic.deps.jfr.okhttp3")
    relocate("org.slf4j", "com.newrelic.deps.jfr.slf4j")
    relocate("com.newrelic.telemetry", "com.newrelic.deps.jfr.telemetry")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.group as String?
            artifactId = "jfr-reporter"
            version = project.version as String?
            from(components["java"])
            pom {
                name.set(project.name)
                description.set("New Relic Java Flight Recorder (JFR) Agent Extension")
                url.set("https://github.com/newrelic/newrelic-jfr-reporter")
                licenses {
                    license {
                        name.set("Proprietary")
                        distribution.set("manual")
                        comments.set("Open Source licenses can be found in LICENSE and THIRD_PARTY_NOTICES.md")
                    }
                }
                developers {
                    developer {
                        id.set("newrelic")
                        name.set("New Relic")
                        email.set("opensource@newrelic.com")
                    }
                }
                scm {
                    url.set("git@github.com:newrelic/newrelic-jfr-reporter.git")
                    connection.set("scm:git:git@github.com:newrelic/newrelic-jfr-reporter.git")
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = project.uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = project.uri("https://oss.sonatype.org/content/repositories/snapshots/")
            url = if (project.version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = System.getenv("SONATYPE_USERNAME")
                if ((username?.length ?: 0) == 0) {
                    username = project.properties["sonatypeUsername"] as String?
                }
                password = System.getenv("SONATYPE_PASSWORD")
                if ((password?.length ?: 0) == 0) {
                    password = project.properties["sonatypePassword"] as String?
                }
            }
        }
    }
}

signing {
    val signingKey: String? = System.getenv("SIGNING_KEY")
    val signingKeyId: String? = System.getenv("SIGNING_KEY_ID")
    val signingPassword: String? = System.getenv("SIGNING_PASSWORD")
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    sign(publishing.publications["mavenJava"])
}
