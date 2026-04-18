/*
 * This file is part of ProCosmetics - https://github.com/FilleDev/ProCosmetics
 * Copyright (C) 2025-2026 FilleDev and contributors
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
package se.filledev.procosmetics.api;

import org.bukkit.plugin.java.JavaPlugin;
import se.filledev.procosmetics.api.config.ConfigManager;
import se.filledev.procosmetics.api.cosmetic.registry.CategoryRegistries;
import se.filledev.procosmetics.api.cosmetic.registry.CosmeticRarityRegistry;
import se.filledev.procosmetics.api.economy.EconomyManager;
import se.filledev.procosmetics.api.locale.LanguageManager;
import se.filledev.procosmetics.api.menu.MenuManager;
import se.filledev.procosmetics.api.nms.NMSManager;
import se.filledev.procosmetics.api.storage.Database;
import se.filledev.procosmetics.api.treasure.TreasureChestManager;
import se.filledev.procosmetics.api.treasure.animation.TreasureChestAnimationRegistry;
import se.filledev.procosmetics.api.user.UserManager;

import java.util.concurrent.Executor;

/**
 * Main API interface for ProCosmetics.
 * This interface provides access to all major subsystems including configuration,
 * localization, user management, database operations, cosmetic registries, and more.
 *
 * <p>Access this instance via {@link ProCosmeticsProvider#get()}.</p>
 *
 * <p>Example usage:
 * <pre>{@code
 * ProCosmetics api = ProCosmeticsProvider.get();
 * UserManager userManager = api.getUserManager();
 * User user = userManager.getConnected(player);
 * }</pre>
 */
public interface ProCosmetics {

    /**
     * Gets the configuration manager for accessing plugin configs.
     *
     * @return the configuration manager
     */
    ConfigManager getConfigManager();

    /**
     * Gets the language manager for translations and localization.
     *
     * @return the language manager
     */
    LanguageManager getLanguageManager();

    /**
     * Gets the user manager for retrieving and managing users.
     *
     * @return the user manager
     */
    UserManager getUserManager();

    /**
     * Gets the menu manager for GUI operations.
     *
     * @return the menu manager
     */
    MenuManager getMenuManager();

    /**
     * Gets the database instance for data persistence operations.
     *
     * @return the database instance
     */
    Database getDatabase();

    /**
     * Gets the NMS manager for version-specific entity operations.
     *
     * @return the nMS manager
     */
    NMSManager getNMSManager();

    /**
     * Gets the cosmetic rarity registry.
     *
     * @return the cosmetic rarity registry
     */
    CosmeticRarityRegistry getCosmeticRarityRegistry();

    /**
     * Gets the category registries containing all cosmetic categories.
     *
     * @return the category registries
     */
    CategoryRegistries getCategoryRegistries();

    /**
     * Gets the animation registry for treasure chests.
     *
     * @return the treasure chest animation registry
     */
    TreasureChestAnimationRegistry getTreasureChestAnimationRegistry();

    /**
     * Gets the treasure chest manager for loot operations.
     *
     * @return the treasure chest manager
     */
    TreasureChestManager getTreasureChestManager();

    /**
     * Gets the economy manager for handling currency operations.
     *
     * @return the economy manager
     */
    EconomyManager getEconomyManager();

    /**
     * Gets the underlying Bukkit JavaPlugin instance.
     *
     * @return the javaPlugin instance
     */
    JavaPlugin getJavaPlugin();

    /**
     * Gets an executor that runs tasks on the main server thread.
     *
     * @return the synchronous executor
     */
    Executor getSyncExecutor();

    /**
     * Gets an executor that runs tasks asynchronously off the main thread.
     *
     * @return the asynchronous executor
     */
    Executor getAsyncExecutor();
}
