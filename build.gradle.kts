import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    java
    id("com.gradleup.shadow") version "8.3.8"
    id("com.diffplug.spotless") version "8.2.1"
}

group = "se.filledev"
version = "2.0.4"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.processResources {
    val projectVersion = project.version
    inputs.property("version", projectVersion)
    filesMatching("**/plugin.yml") {
        filter<ReplaceTokens>("tokens" to mapOf("VERSION" to projectVersion.toString()))
    }
}

// Configure shadow jar
tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("")

    // Relocate libs that are not exposed in the API module
    val basePackage = "se.filledev.procosmetics.libs"

    val relocations = mapOf(
        "dev.dejvokep.boostedyaml" to "boostedyaml",
        "com.zaxxer.hikari" to "hikari",
        "com.mongodb" to "mongodb",
        "redis.clients" to "jedis",
        "org.bstats" to "bstats"
    )
    relocations.forEach { (from, to) -> relocate(from, "$basePackage.$to") }
}

// Make build depend on shadowJar instead of jar
tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}

// Disable the default jar task since we're using shadowJar
tasks.named<Jar>("jar") {
    enabled = false
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    subprojects.forEach {
        implementation(project(it.path))
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.diffplug.spotless")

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://jitpack.io")

        // Spigot
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

        // Paper
        maven("https://papermc.io/repo/maven-public/")
    }
    val javaVersion = findProperty("javaVersion")?.toString()?.toIntOrNull() ?: 25

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        javaCompiler.set(javaToolchains.compilerFor {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        })
    }
    // Configure Spotless for all subprojects
    spotless {
        java {
            target("src/**/*.java")
            trimTrailingWhitespace()
            endWithNewline()
            removeUnusedImports()
            //palantirJavaFormat("2.81.0").style("GOOGLE").formatJavadoc(true)
            licenseHeaderFile(rootProject.file("/config/spotless/license-header.txt"), "package ")
                .updateYearWithLatest(true)
        }
    }

    tasks.named("build") {
        dependsOn("spotlessApply")
    }
}

tasks.register("copyJarToPluginsFolder") {
    val folderPath = providers.environmentVariable("JarFolderPath")
    val projectName = project.name
    val projectVersion = project.version.toString()

    onlyIf { folderPath.isPresent }

    dependsOn(tasks.named("shadowJar"))

    doLast {
        copy {
            from("build/libs/$projectName-$projectVersion.jar")
            into(folderPath.get())
            rename { it.replace("-$projectVersion", "") }
        }
    }
}