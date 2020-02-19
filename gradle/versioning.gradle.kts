import java.util.regex.Pattern

tasks.register("version") {
    doLast {
        println(project.version)
    }
}

tasks.register("bumpVersion") {
    doLast {
        val versionParts = project.version.toString().split('.')
        val major = versionParts[0]
        val minor = versionParts[1]
        val patch = versionParts[2]

        val nextPatch = patch.toInt() + 1
        val nextVersion = listOf(major, minor, nextPatch).joinToString(".")

        val previousVersionRegex = "^version=${Pattern.quote(project.version.toString())}$".toRegex()
        val updated = file("$projectDir/gradle.properties").readText().replace(previousVersionRegex, "version=$nextVersion")
        file("$projectDir/gradle.properties").writeText(updated)
    }
}
