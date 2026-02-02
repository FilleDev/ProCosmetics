/*
 * This file is part of ProCosmetics - https://github.com/FilleDev/ProCosmetics
 * Copyright (C) 2025 FilleDev and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package se.filledev.procosmetics;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import se.filledev.procosmetics.api.ProCosmetics;
import se.filledev.procosmetics.api.ProCosmeticsProvider;
import se.filledev.procosmetics.api.config.Config;
import se.filledev.procosmetics.api.cosmetic.registry.CategoryRegistries;
import se.filledev.procosmetics.api.cosmetic.registry.CosmeticRarityRegistry;
import se.filledev.procosmetics.api.storage.Database;
import se.filledev.procosmetics.api.treasure.TreasureChestPlatform;
import se.filledev.procosmetics.api.treasure.animation.TreasureChestAnimationRegistry;
import se.filledev.procosmetics.api.user.User;
import se.filledev.procosmetics.command.CommandBase;
import se.filledev.procosmetics.command.SimpleCommand;
import se.filledev.procosmetics.command.commands.*;
import se.filledev.procosmetics.config.ConfigManagerImpl;
import se.filledev.procosmetics.cosmetic.registry.CategoryRegistriesImpl;
import se.filledev.procosmetics.cosmetic.registry.CosmeticRarityRegistryImpl;
import se.filledev.procosmetics.economy.EconomyManagerImpl;
import se.filledev.procosmetics.listener.*;
import se.filledev.procosmetics.listener.hook.cmi.CMIVanishListener;
import se.filledev.procosmetics.listener.hook.essentials.EssentialsVanishListener;
import se.filledev.procosmetics.listener.hook.premiumvanish.PremiumVanishListener;
import se.filledev.procosmetics.locale.LanguageManagerImpl;
import se.filledev.procosmetics.menu.MenuManagerImpl;
import se.filledev.procosmetics.nms.NMSManagerImpl;
import se.filledev.procosmetics.packet.PacketManager;
import se.filledev.procosmetics.placeholder.PlaceholderManager;
import se.filledev.procosmetics.redis.RedisManager;
import se.filledev.procosmetics.storage.DatabaseTypeProvider;
import se.filledev.procosmetics.treasure.TreasureChestManagerImpl;
import se.filledev.procosmetics.treasure.animation.TreasureChestAnimationRegistryImpl;
import se.filledev.procosmetics.user.UserManagerImpl;
import se.filledev.procosmetics.util.LogUtil;
import se.filledev.procosmetics.util.ResourceExporter;
import se.filledev.procosmetics.util.block.FakeBlockManager;
import se.filledev.procosmetics.util.version.VersionUtil;
import se.filledev.procosmetics.worldguard.WorldGuardManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProCosmeticsPlugin extends JavaPlugin implements ProCosmetics {

    private static ProCosmeticsPlugin plugin;
    private BukkitAudiences adventure;

    private Logger logger;
    private Executor syncExecutor;
    private Executor ayncExecutor;
    private ConfigManagerImpl configManager;
    private LanguageManagerImpl languageManager;
    private NMSManagerImpl nmsManager;
    private CosmeticRarityRegistryImpl cosmeticRarityRegistry;
    private CategoryRegistriesImpl categoryRegistries;
    private UserManagerImpl userManager;
    private TreasureChestAnimationRegistry treasureChestAnimationRegistry;
    private TreasureChestManagerImpl treasureChestManager;
    private MenuManagerImpl menuManager;
    private PacketManager packetManager;
    private FakeBlockManager fakeBlockManager;
    private EconomyManagerImpl economyManager;
    private PlaceholderManager placeholderManager;
    private CommandBase commandBase;
    private RedisManager redisManager;
    private Database database;
    private WorldGuardManager worldGuardManager;

    @Override
    public void onLoad() {
        ProCosmeticsPlugin.plugin = this;
        logger = getLogger();
        syncExecutor = runnable -> getServer().getScheduler().runTask(this, runnable);
        ayncExecutor = runnable -> getServer().getScheduler().runTaskAsynchronously(this, runnable);

        if (!VersionUtil.isSupported()) {
            LogUtil.printUnsupported();
            return;
        }
        logger.info("Initializing...");

        ResourceExporter.export(this);

        configManager = new ConfigManagerImpl(this);
        languageManager = new LanguageManagerImpl(this);
        nmsManager = new NMSManagerImpl(this);
        cosmeticRarityRegistry = new CosmeticRarityRegistryImpl(this);
        categoryRegistries = new CategoryRegistriesImpl(this);
        userManager = new UserManagerImpl(this);
        treasureChestAnimationRegistry = new TreasureChestAnimationRegistryImpl();
        treasureChestManager = new TreasureChestManagerImpl(this);
        menuManager = new MenuManagerImpl(this);
        packetManager = new PacketManager(this);
        fakeBlockManager = new FakeBlockManager(this);
        economyManager = new EconomyManagerImpl(this);
        placeholderManager = new PlaceholderManager(this);
        commandBase = new CommandBase(this);

        initializeRedis();
        initializeDatabase();
        initializeMetrics();
        preHookPlugins();
    }

    @Override
    public void onEnable() {
        adventure = BukkitAudiences.create(this);

        if (!VersionUtil.isSupported()) {
            LogUtil.printUnsupported();
            commandBase = new CommandBase(this);
            registerCommands(new UnsupportedCommand(this));
            return;
        }
        economyManager.hookPlugin();

        registerListeners();
        registerCommands();
        hookPlugins();
        checkUpdate();

        NoteBlockAPI.init(this);

        userManager.loadOnlinePlayers();
        if (redisManager != null) {
            redisManager.registerChannels();
        }
        ProCosmeticsProvider.register(this);

        logger.info("Initialized!");
    }

    private void registerListeners() {
        userManager.registerListeners();
        treasureChestManager.registerListeners();
        menuManager.registerListeners();
        packetManager.registerListeners();

        registerListeners(new BlockListener(),
                new CosmeticItemListener(this),
                new CosmeticListener(this),
                new CreatureSpawnListener(),
                new EntityListener(),
                new FallDamageListener(this),
                new InventoryListener(),
                new PlayerListener(this)
        );
    }

    @Override
    public void onDisable() {
        if (!VersionUtil.isSupported()) {
            LogUtil.printUnsupported();
            return;
        }
        if (redisManager != null) {
            redisManager.shutdown();
        }

        for (User user : userManager.getAllConnected()) {
            user.clearAllCosmetics(true, false);
        }
        database.shutdown();

        for (TreasureChestPlatform platform : treasureChestManager.getPlatforms()) {
            platform.hideDisplay();
        }
        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);

        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
        NoteBlockAPI.getAPI().shutdown();
    }

    private void initializeRedis() {
        if (configManager.getMainConfig().getBoolean("redis.enabled")) {
            redisManager = new RedisManager(this);
        }
    }

    private void initializeDatabase() {
        database = DatabaseTypeProvider.createDatabase(plugin, configManager.getMainConfig().getString("storage.type"));
    }

    private void registerCommands() {
        commandBase.getCommands().clear();

        registerCommands(new CosmeticsCommand(this),
                new EquipCommand(this),
                new UnequipCommand(this),
                new UnequipAllCommand(this),
                new ToggleSelfViewCommand(this),
                new ProCosmeticsCommand(this)
        );
    }

    private void preHookPlugins() {
        PluginManager pluginManager = getServer().getPluginManager();

        if (pluginManager.getPlugin("WorldGuard") != null && configManager.getMainConfig().getBoolean("world_guard.enabled")) {
            logger.info("Hooking into WorldGuard...");
            this.worldGuardManager = new WorldGuardManager(this);
        }
    }

    private void hookPlugins() {
        PluginManager pluginManager = getServer().getPluginManager();

        if (pluginManager.getPlugin("Essentials") != null) {
            logger.info("Hooking into Essentials...");
            registerListeners(new EssentialsVanishListener(this));
        }
        if (pluginManager.getPlugin("CMI") != null) {
            logger.info("Hooking into CMI...");
            registerListeners(new CMIVanishListener(this));
        }
        if (pluginManager.getPlugin("PremiumVanish") != null) {
            logger.info("Hooking into PremiumVanish...");
            registerListeners(new PremiumVanishListener(this));
        }
        if (pluginManager.getPlugin("SuperVanish") != null) {
            logger.info("Hooking into SuperVanish...");
            registerListeners(new PremiumVanishListener(this));
        }

        if (worldGuardManager != null) {
            worldGuardManager.register(this);
        }
    }

    private void initializeMetrics() {
        Config config = configManager.getMainConfig();

        if (config.getBoolean("settings.enable_metrics")) {
            Metrics metrics = new Metrics(this, 6408);
            metrics.addCustomChart(new SimplePie("database", () -> database.getType()));
            metrics.addCustomChart(new SimplePie("economy", () -> economyManager.getType().getName()));
            metrics.addCustomChart(new SimplePie("world-guard", () -> worldGuardManager != null ? "Yes" : "No"));
        }
    }

    private void checkUpdate() {
        if (configManager.getMainConfig().getBoolean("settings.check_updates")) {
            ayncExecutor.execute(() -> {
                try {
                    URLConnection urlConnection = URI.create("https://api.spigotmc.org/legacy/update.php?resource=49106").toURL().openConnection();
                    urlConnection.setConnectTimeout(1000);

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                        String version = getDescription().getVersion();
                        String line = reader.readLine();

                        if (!line.equalsIgnoreCase(version)) {
                            getLogger().log(Level.INFO, "There is a newer version available for " + getName() + ". Currently running " + version + " and the latest release is " + line + ".");
                        }
                    }
                } catch (IOException e) {
                    getLogger().log(Level.WARNING, "Failed to check for plugin updates.");
                }
            });
        }
    }

    private void registerListeners(Listener... listeners) {
        PluginManager pluginManager = getServer().getPluginManager();

        for (Listener listener : listeners) {
            if (listener != null) {
                pluginManager.registerEvents(listener, this);
            }
        }
    }

    @SafeVarargs
    private void registerCommands(SimpleCommand<CommandSender>... commands) {
        for (SimpleCommand<CommandSender> command : commands) {
            commandBase.registerCommand(command);
        }
    }

    public static ProCosmeticsPlugin getPlugin() {
        return plugin;
    }

    public BukkitAudiences adventure() {
        if (adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public Executor getSyncExecutor() {
        return syncExecutor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return ayncExecutor;
    }

    @Override
    public ConfigManagerImpl getConfigManager() {
        return configManager;
    }

    @Override
    public LanguageManagerImpl getLanguageManager() {
        return languageManager;
    }

    @Override
    public NMSManagerImpl getNMSManager() {
        return nmsManager;
    }

    @Override
    public CosmeticRarityRegistry getCosmeticRarityRegistry() {
        return cosmeticRarityRegistry;
    }

    @Override
    public CategoryRegistries getCategoryRegistries() {
        return categoryRegistries;
    }

    @Override
    public TreasureChestAnimationRegistry getTreasureChestAnimationRegistry() {
        return treasureChestAnimationRegistry;
    }

    @Override
    public UserManagerImpl getUserManager() {
        return userManager;
    }

    @Override
    public TreasureChestManagerImpl getTreasureChestManager() {
        return treasureChestManager;
    }

    @Override
    public MenuManagerImpl getMenuManager() {
        return menuManager;
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }

    public FakeBlockManager getBlockRestoreManager() {
        return fakeBlockManager;
    }

    @Override
    public EconomyManagerImpl getEconomyManager() {
        return economyManager;
    }

    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    public CommandBase getCommandBase() {
        return commandBase;
    }

    public RedisManager getRedisManager() {
        return redisManager;
    }

    @Override
    public Database getDatabase() {
        return database;
    }

    public WorldGuardManager getWorldGuardManager() {
        return worldGuardManager;
    }
}
