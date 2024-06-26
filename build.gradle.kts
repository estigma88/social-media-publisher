plugins {
    java
    id("io.freefair.lombok") version "8.6"
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
}

extra["springCloudVersion"] = "2023.0.1"

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

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

dependencies {
    implementation(libs.org.springframework.boot.spring.boot.starter.web)
    implementation(libs.org.springframework.boot.spring.boot.starter.security)
    implementation(libs.org.springframework.boot.spring.boot.starter.mail)
    implementation(libs.org.springframework.boot.spring.boot.starter.oauth2.client)
    implementation(libs.org.twitter4j.twitter4j.core)
    implementation(libs.io.github.boostchicken.spring.data.dynamodb)
    implementation(libs.com.amazonaws.serverless.aws.serverless.java.container.springboot3)

    itestImplementation(libs.org.springframework.cloud.spring.cloud.starter.contract.stub.runner)
    itestImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    itestImplementation(libs.io.rest.assured.rest.assured)
    itestImplementation(libs.org.mockito.mockito.core)

}

group = "com.coderstower"
version = "0.0.8-SNAPSHOT"
description = "social-media-publisher"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register("zip", Zip::class) {
    description = "Zip to deploy to lambda."
    group = "build"

    from(sourceSets.main.get().output)
    into("lib") {
        from(configurations.runtimeClasspath)
    }
}

