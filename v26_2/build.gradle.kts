dependencies {
    compileOnly("org.spigotmc:spigot:26.2-R0.1-SNAPSHOT")

    compileOnly("net.kyori:adventure-text-serializer-legacy:5.2.0")

    implementation(project(":api"))
    implementation(project(":core"))
}
