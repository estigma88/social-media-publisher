plugins {
    java
    id("io.freefair.lombok") version "8.6"
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
}

repositories {
    mavenCentral()
}

sourceSets {
    create("itest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val itestImplementation by configurations.getting {
    extendsFrom(configurations.implementation.get())
}
val itestRuntimeOnly by configurations.getting

configurations["itestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

val itest = task<Test>("itest") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["itest"].output.classesDirs
    classpath = sourceSets["itest"].runtimeClasspath
    shouldRunAfter("test")

    useJUnitPlatform()

    testLogging {
        events("passed")
    }
}

tasks.check { dependsOn(itest) }

dependencies {
    implementation(libs.org.springframework.boot.spring.boot.starter.web)
    implementation(libs.org.springframework.boot.spring.boot.starter.security)
    implementation(libs.org.springframework.boot.spring.boot.starter.oauth2.client)
    implementation(libs.org.twitter4j.twitter4j.core)
    implementation(libs.io.github.boostchicken.spring.data.dynamodb)
    implementation(libs.com.amazonaws.serverless.aws.serverless.java.container.springboot2)
    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    testImplementation(libs.org.springframework.security.spring.security.test)
    itestImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    itestImplementation("org.testcontainers:localstack")

}

group = "com.coderstower"
version = "0.0.6-SNAPSHOT"
description = "social-media-publisher"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

tasks.withType<Test> {
    useJUnitPlatform()
}
