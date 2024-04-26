/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("io.freefair.lombok") version "8.6"
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://s3-us-west-2.amazonaws.com/dynamodb-local/release")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    api(libs.org.springframework.boot.spring.boot.starter.web)
    api(libs.org.springframework.boot.spring.boot.starter.security)
    api(libs.org.springframework.boot.spring.boot.starter.oauth2.client)
    api(libs.org.twitter4j.twitter4j.core)
    api(libs.com.amazonaws.serverless.aws.serverless.java.container.springboot2)
    api(libs.io.github.boostchicken.spring.data.dynamodb)
    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    testImplementation(libs.com.amazonaws.dynamodblocal)
    testImplementation(libs.org.junit.pioneer.junit.pioneer)
    testImplementation(libs.org.springframework.security.spring.security.test)
//    compileOnly(libs.org.projectlombok.lombok)
}

group = "com.coderstower"
version = "0.0.6-SNAPSHOT"
description = "social-media-publisher"
java.sourceCompatibility = JavaVersion.VERSION_1_8

//publishing {
//    publications.create<MavenPublication>("maven") {
//        from(components["java"])
//    }
//}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}
