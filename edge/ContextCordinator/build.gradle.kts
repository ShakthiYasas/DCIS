plugins {
    id("java")
    id("java-library")
    id("maven-publish")
}

group = "org.dcis"
version = "1.0-DEV"

repositories {
    mavenCentral()
    flatDir {
        dirs("libs")
    }
}

dependencies {
    compileOnly("org.dcis:GRPC:1.0-DEV")
    runtimeOnly("org.dcis:GRPC:1.0-DEV")
    implementation("org.json:json:20240303")
    implementation("io.grpc:grpc-api:1.68.1")
    implementation("io.grpc:grpc-stub:1.68.1")
    implementation("io.grpc:grpc-core:1.68.1")
    implementation("io.grpc:grpc-netty-shaded:1.68.1")
    implementation("com.google.protobuf:protobuf-java:4.28.3")
    api("com.fasterxml.jackson.core:jackson-databind:2.18.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

publishing {
    publications {
        create<MavenPublication>("contextcordinator") {
            from(components["java"])
        }
    }
}