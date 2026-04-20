plugins {
    id("java-library")
    id("signing")
    id("com.vanniktech.maven.publish") version "0.36.0"
}

group = "se.filledev"
version = "2.0.0"

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
}

dependencies {
    // Spigot API
    compileOnly("org.spigotmc:spigot-api:26.1.1-R0.1-SNAPSHOT")

    compileOnlyApi("net.kyori:adventure-api:5.0.1")
    compileOnlyApi("net.kyori:adventure-text-minimessage:5.0.1")
    compileOnlyApi("it.unimi.dsi:fastutil:8.5.18")

    // Annotations
    compileOnlyApi("org.jetbrains:annotations:26.1.0")

    // NoteBlockAPI
    compileOnly("com.github.FilleDev:NoteBlockAPI:1c5500b038")

}

publishing {
    repositories {
        maven {
            name = "reposilite"
            url = uri("https://repo.filledev.se/releases")

            credentials {
                username = providers.gradleProperty("reposiliteUsername").get()
                password = providers.gradleProperty("reposiliteToken").get()
            }
        }
    }
}

mavenPublishing {
    //publishToMavenCentral()

    signAllPublications()

    coordinates(
        groupId = "se.filledev",
        artifactId = "procosmetics-api",
        version = version.toString()
    )

    pom {
        name.set("ProCosmetics API")
        description.set("Public API for the ProCosmetics Minecraft plugin.")
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
