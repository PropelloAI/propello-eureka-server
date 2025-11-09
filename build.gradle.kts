import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.spring") version "2.1.20"
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.propello"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven {
        name = "Spring Milestone"
        url = uri("https://repo.spring.io/milestone")
    }
    maven {
        name = "Spring Snapshots"
        url = uri("https://repo.spring.io/snapshot")
    }
    maven {
        name = "GitHubPackages-Platform"
        url = uri("https://maven.pkg.github.com/PropelloAI/propello-platform")
        credentials {
            username = System.getenv("GITHUB_ACTOR") ?: findProperty("gpr.user") as String?
            password = System.getenv("GITHUB_TOKEN") ?: findProperty("gpr.token") as String?
        }
    }
}

dependencies {
    // Propello Platform BOM - version from gradle.properties
    val platformVersion = project.findProperty("propello.platform.version") as String? ?: "1.0.26"
    implementation(platform("com.propello:propello-platform:$platformVersion"))

    // Eureka Server (from platform catalog)
    implementation(libs.spring.cloud.starter.netflix.eureka.server)

    // Spring Boot Actuator for monitoring (from platform catalog)
    implementation(libs.spring.boot.starter.actuator)

    // Kotlin (from platform catalog)
    implementation(libs.bundles.kotlin)
    implementation(libs.jackson.module.kotlin)

    // Testing (from platform catalog)
    testImplementation(libs.spring.boot.starter.test)
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
