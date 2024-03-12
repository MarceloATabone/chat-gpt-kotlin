plugins {
    application
    kotlin("jvm") version libs.versions.kotlin
    kotlin("plugin.serialization") version libs.versions.serialization
    id("io.ktor.plugin") version libs.versions.ktor
}

group = "com.gpt-chat-backend"
version = "0.0.1"

application {
    mainClass.set("application.ApplicationKt")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}
repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    // Ktor
    implementation(libs.ktor.server.netty.jvm)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.content.negotiation.jvm)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.cors)

    // Postgres
    implementation(libs.postgres.exposed.core)


    //Others
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger)

}