dependencies {
    compileOnly("org.spigotmc:spigot:26.1.2-R0.1-SNAPSHOT")

    compileOnly("net.kyori:adventure-text-serializer-legacy:4.26.1")

    implementation(project(":api"))
    implementation(project(":core"))
}
