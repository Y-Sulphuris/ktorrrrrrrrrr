val kotlin_version: String by project
val ktor_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
    id("io.ktor.plugin") version "3.4.0"
    application
}

group = "com.example"
version = "0.0.1"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.example.ApplicationKt")
}

dependencies {

    // Ktor BOM (чтобы все ktor зависимости были одной версии)
    implementation(platform("io.ktor:ktor-bom:$ktor_version"))

    // Swagger | OpenAPI
    implementation("io.ktor:ktor-server-swagger")
    implementation("io.ktor:ktor-server-openapi")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.19")

    // Ktor
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-status-pages")

    // Content Negotiation
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")

    // Authentication
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-auth-jwt")

    // Routing OpenAPI
    implementation("io.ktor:ktor-server-routing-openapi")

    // Database
    implementation("org.jetbrains.exposed:exposed-core:0.44.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.44.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.44.0")
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")

    // Password Hashing
    implementation("at.favre.lib:bcrypt:0.10.2")
    implementation("org.mindrot:jbcrypt:0.4")

    // JWT
    implementation("com.auth0:java-jwt:4.4.0")
    // Redis
    implementation("io.lettuce:lettuce-core:6.3.2.RELEASE")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // Tests
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")
}

tasks.withType<Test> {
    useJUnitPlatform()
}