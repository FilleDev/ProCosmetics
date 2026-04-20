repositories {
    // Plugins
    maven("https://repo.essentialsx.net/releases/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://ci.ender.zone/plugin/repository/everything/")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://maven.enginehub.org/repo/") // WorldGuard
}

dependencies {
    // Spigot API
    compileOnly("org.spigotmc:spigot:26.1.1-R0.1-SNAPSHOT") // Includes libs
    compileOnly("org.spigotmc:spigot-api:26.1.1-R0.1-SNAPSHOT")

    // Project dependencies
    implementation(project(":api"))
    compileOnly("org.jetbrains:annotations:26.0.2-1")

    // Runtime libraries (will be shaded)
    implementation("dev.dejvokep:boosted-yaml:1.3.7")
    implementation("com.github.FilleDev:NoteBlockAPI:1c5500b038")
    implementation("org.mongodb:mongodb-driver-sync:5.6.3")
    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation("redis.clients:jedis:7.2.1")
    implementation("org.bstats:bstats-bukkit:3.1.0")
    compileOnly("org.xerial:sqlite-jdbc:3.50.3.0") // Included in Spigot

    implementation("net.kyori:adventure-api:5.0.1")
    implementation("net.kyori:adventure-text-minimessage:5.0.1")
    implementation("net.kyori:adventure-platform-bukkit:4.4.1")

    // Plugin hooks
    compileOnly("me.clip:placeholderapi:2.12.2")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("net.essentialsx:EssentialsX:2.21.2")
    {
        exclude(group = "io.papermc.paper")
    }
    compileOnly("com.github.Zrips:CMI-API:9.7.14.3")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.17-SNAPSHOT")
    compileOnly("org.black_ixx:playerpoints:3.3.3")
    compileOnly("com.github.LeonMangler:SuperVanish:6.2.19")
}