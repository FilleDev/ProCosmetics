plugins {
    java
    `maven-publish`
    signing
}

group = "se.filledev"
version = "1.0.0"

java {
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    // Spigot API
    compileOnly("org.spigotmc:spigot-api:1.21.10-R0.1-SNAPSHOT")

    compileOnly("net.kyori:adventure-api:4.26.1")
    compileOnly("net.kyori:adventure-text-minimessage:4.26.1")
    compileOnly("it.unimi.dsi:fastutil:8.5.18")
    compileOnly("io.netty:netty-handler:4.2.9.Final")

    // Annotations
    compileOnly("org.jetbrains:annotations:26.0.2-1")

    // NoteBlockAPI
    compileOnly("com.github.FilleDev:NoteBlockAPI:1c5500b038")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = group.toString()
            artifactId = "ProCosmetics-api"
            version = version.toString()

            pom {
                name.set("ProCosmetics API")
                description.set("API module for the ProCosmetics Minecraft plugin (Java Edition).")
                url.set("https://github.com/filledev/ProCosmetics")

                licenses {
                    license {
                        name.set("GNU General Public License v3.0")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
                    }
                }

                developers {
                    developer {
                        id.set("se.filledev")
                        name.set("FilleDev")
                        //email.set("email@example.com") // optional
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/FilleDev/ProCosmetics.git")
                    developerConnection.set("scm:git:ssh://git@github.com:FilleDev/ProCosmetics.git")
                    url.set("https://github.com/FilleDev/ProCosmetics")
                }
            }
        }
    }

    repositories {
        maven {
            name = "CentralPortal"
            url = uri("https://central.sonatype.com/api/v1/publisher/upload")

            val username = findProperty("centralUsername") as? String
            val password = findProperty("centralPassword") as? String

            if (username != null && password != null) {
                credentials {
                    this.username = username
                    this.password = password
                }
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["mavenJava"])
}