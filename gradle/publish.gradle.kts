apply(plugin = "maven-publish")
apply(plugin = "signing")

tasks.create<Jar>("sourcesJar") {
    archiveClassifier.set("sources")

    manifest {
        attributes.putAll(mapOf(
                "Implementation-Title" to "Empty Source File",
                "Implementation-Version" to archiveVersion.get(),
                "Created-By" to "New Relic, Inc",
                "Specification-Version" to archiveVersion.get(),
                "Build-Id" to "None"
        ))
    }
}

tasks.create<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")

    manifest {
        attributes.putAll(mapOf(
                "Implementation-Title" to "Empty Source File",
                "Implementation-Version" to archiveVersion.get(),
                "Created-By" to "New Relic, Inc",
                "Specification-Version" to archiveVersion.get(),
                "Build-Id" to "None"
        ))
    }
}

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            pom {
                url.set("https://www.newrelic.com/")
                scm {
                    url.set("https://github.com/newrelic")
                }
                name.set(project.name)
                description.set("A library for sending JFR profiling data to New Relic.")
                licenses {
                    license {
                        name.set("New Relic agent license")
                        url.set("https://docs.newrelic.com/docs/licenses/license-information/distributed-licenses/new-relic-agent-license")
                    }
                }
                developers {
                    developer {
                        id.set("newrelic")
                        name.set("New Relic")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

            configure<SigningExtension> {
                sign(publications["mavenJava"])
            }

            credentials {
                username = project.properties["sonatypeUsername"] as String?
                password = project.properties["sonatypePassword"] as String?
            }
        }
    }
}