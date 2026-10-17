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
package se.filledev.procosmetics.vanish;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;
import se.filledev.procosmetics.ProCosmeticsPlugin;
import se.filledev.procosmetics.api.user.User;
import se.filledev.procosmetics.util.AbstractRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class VanishManager {

    // Metadata set by most vanish plugins (SuperVanish, PremiumVanish, Essentials, VanishNoPacket etc.)
    private static final String VANISHED_METADATA = "vanished";

    private final ProCosmeticsPlugin plugin;
    private final List<VanishHook> hooks = new ArrayList<>();
    private final Set<UUID> hiddenPlayers = new HashSet<>();

    public VanishManager(ProCosmeticsPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerListeners() {
        plugin.getServer().getPluginManager().registerEvents(new Listeners(), plugin);
        // Catches vanish plugins without a hook and plugins that vanish players without calling an event
        plugin.getServer().getScheduler().runTaskTimer(plugin, new VanishRunnable(), 20L, 20L);
    }

    public void registerHook(VanishHook hook) {
        hooks.add(hook);
    }

    public boolean isVanished(Player player) {
        for (MetadataValue value : player.getMetadata(VANISHED_METADATA)) {
            if (value.asBoolean()) {
                return true;
            }
        }

        for (VanishHook hook : hooks) {
            if (hook.isVanished(player)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Hides the cosmetics of a player that is about to vanish. Vanish plugins call their vanish
     * events before the player is vanished, so the vanish state cannot be checked here.
     */
    public void hideCosmetics(Player player) {
        if (!player.isOnline()) {
            return;
        }
        User user = plugin.getUserManager().getConnected(player);

        if (user == null) {
            return;
        }
        hiddenPlayers.add(player.getUniqueId());
        user.unequipCosmetics(true, false);
    }

    /**
     * Updates the cosmetics on the next tick, when the vanish plugin has applied the new vanish state.
     */
    public void updateCosmeticsLater(Player player) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (player.isOnline()) {
                updateCosmetics(player);
            }
        });
    }

    public void updateCosmetics(Player player) {
        User user = plugin.getUserManager().getConnected(player);

        if (user != null) {
            updateCosmetics(user, player);
        }
    }

    private void updateCosmetics(User user, Player player) {
        UUID uuid = player.getUniqueId();

        if (isVanished(player)) {
            if (hiddenPlayers.add(uuid)) {
                user.unequipCosmetics(true, false);
            }
        } else if (hiddenPlayers.remove(uuid) && player.getGameMode() != GameMode.SPECTATOR) {
            user.equipSavedCosmetics(true);
        }
    }

    private final class Listeners implements Listener {

        // Must run before the saved cosmetics are equipped in UserManagerImpl (HIGH)
        @EventHandler(priority = EventPriority.NORMAL)
        private void onJoin(PlayerJoinEvent event) {
            updateCosmetics(event.getPlayer());
        }

        @EventHandler(priority = EventPriority.MONITOR)
        private void onQuit(PlayerQuitEvent event) {
            hiddenPlayers.remove(event.getPlayer().getUniqueId());
        }
    }

    private final class VanishRunnable extends AbstractRunnable {

        @Override
        public void run() {
            for (User user : plugin.getUserManager().getAllConnected()) {
                Player player = user.getPlayer();

                if (player != null) {
                    updateCosmetics(user, player);
                }
            }
        }
    }
}
