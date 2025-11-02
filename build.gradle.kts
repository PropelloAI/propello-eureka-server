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
}

extra["springCloudVersion"] = "2024.0.0"

dependencies {
    // Propello Platform BOM
    val platformVersion = project.findProperty("propello.platform.version") as String? ?: "1.0.9"
    implementation(platform("com.propello:propello-platform:$platformVersion"))

    // Eureka Server
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")

    // Spring Boot Actuator for monitoring
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
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
