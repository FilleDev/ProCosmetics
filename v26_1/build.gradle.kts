dependencies {
    compileOnly("org.spigotmc:spigot:26.1.2-R0.1-SNAPSHOT")

    compileOnly("net.kyori:adventure-api:5.1.1")
    compileOnly("net.kyori:adventure-platform-bukkit:4.4.1")

    implementation(project(":api"))
    implementation(project(":core"))
}
