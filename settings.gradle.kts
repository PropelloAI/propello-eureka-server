rootProject.name = "propello-eureka-server"

// Auto-detect and include propello-platform for local development
val platformPath = file("../propello-platform")
if (platformPath.exists() && platformPath.isDirectory) {
    includeBuild(platformPath) {
        dependencySubstitution {
            substitute(module("com.propello:propello-platform")).using(project(":"))
        }
    }
    logger.lifecycle("✓ Using local propello-platform from: ${platformPath.absolutePath}")
} else {
    logger.lifecycle("⚠ Local propello-platform not found at: ${platformPath.absolutePath}")
    logger.lifecycle("  Will use published platform BOM from GitHub Packages")
}

dependencyResolutionManagement {
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
                username = System.getenv("GITHUB_ACTOR") ?: providers.gradleProperty("gpr.user").orNull
                password = System.getenv("GITHUB_TOKEN") ?: providers.gradleProperty("gpr.token").orNull
            }
        }
    }
}
