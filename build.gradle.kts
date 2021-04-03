repositories {
    mavenCentral()
}

plugins {
    `maven-publish`
    kotlin("jvm")
    id("org.ajoberstar.grgit") version "4.1.0"
    id("com.utopia-rise.maven-central-publish")
}

val baseVersion = "0.0.1"

val currentCommit: Commit = grgit.head()
// check if the current commit is tagged
var tagOnCurrentCommit = grgit.tag.list().firstOrNull { tag -> tag.commit.id == currentCommit.id }
var releaseMode = tagOnCurrentCommit != null

version = if (!releaseMode) {
    "$baseVersion-${currentCommit.abbreviatedId}-SNAPSHOT"
} else {
    requireNotNull(tagOnCurrentCommit).name
}

dependencies {
    testImplementation("junit:junit:4.12")
}

publishing {
    publications {
        val godotLibraryPublication by creating(MavenPublication::class) {
            artifactId = "jvm-godot-resource-serialization"
            description = "Godot resource data model serialization api for JVM"
            from(components.getByName("java"))
        }
    }
}
