repositories {
    mavenCentral()
}

plugins {
    `maven-publish`
    kotlin("jvm")
    id("org.ajoberstar.grgit") version "4.1.0"
    id("com.utopia-rise.maven-central-publish")
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
