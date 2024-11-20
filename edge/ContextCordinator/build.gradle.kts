plugins {
    id("idea")
    id("java")
    id("java-library")
    id("maven-publish")
}

group = "org.dcis"
version = "1.0-DEV"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.json:json:20240303")
    api("com.fasterxml.jackson.core:jackson-databind:2.18.1")

    testCompileOnly("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

publishing {
    publications {
        create<MavenPublication>("contextcordinator") {
            from(components["java"])
        }
    }
}