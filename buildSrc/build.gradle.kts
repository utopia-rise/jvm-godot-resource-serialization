plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("gradle-plugin", version = "1.3.72"))
    implementation(kotlin("gradle-plugin", version = "1.4.20"))
}
