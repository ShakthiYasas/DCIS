plugins {
    id("java-library")
    id("maven-publish")
}

group = "org.dcis"
version = "1.0-DEV"

repositories {
    mavenCentral()
}

dependencies {
    api("com.fasterxml.jackson.core:jackson-databind:2.18.1")
    implementation("com.google.http-client:google-http-client:1.45.1")
}

publishing {
    publications {
        create<MavenPublication>("mobileedge") {
            from(components["java"])
        }
    }
}