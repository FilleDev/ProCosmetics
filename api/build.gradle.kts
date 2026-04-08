plugins {
    id("java")
    id("signing")
    id("com.vanniktech.maven.publish") version "0.36.0"
}

group = "se.filledev"
version = "2.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spigot API
    compileOnly("org.spigotmc:spigot-api:26.1.1-R0.1-SNAPSHOT")

    compileOnly("net.kyori:adventure-api:4.26.1")
    compileOnly("net.kyori:adventure-text-minimessage:4.26.1")
    compileOnly("it.unimi.dsi:fastutil:8.5.18")
    compileOnly("io.netty:netty-handler:4.2.10.Final")

    // Annotations
    compileOnly("org.jetbrains:annotations:26.0.2-1")

    // NoteBlockAPI
    compileOnly("com.github.FilleDev:NoteBlockAPI:1c5500b038")
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(
        groupId = "se.filledev",
        artifactId = "procosmetics-api",
        version = version.toString()
    )

    pom {
        name.set("ProCosmetics API")
        description.set("A cosmetics plugin for Minecraft servers.")
        url.set("https://github.com/FilleDev/ProCosmetics")

        licenses {
            license {
                name.set("GNU General Public License v3.0")
                url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
            }
        }

        developers {
            developer {
                id.set("filledev")
                name.set("FilleDev")
            }
        }

        scm {
            url.set("https://github.com/FilleDev/ProCosmetics")
            connection.set("scm:git:https://github.com/FilleDev/ProCosmetics.git")
            developerConnection.set("scm:git:ssh://git@github.com:FilleDev/ProCosmetics.git")
        }
    }
}

signing {
    useGpgCmd()
}