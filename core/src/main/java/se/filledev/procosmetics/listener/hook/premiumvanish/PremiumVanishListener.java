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
package se.filledev.procosmetics.listener.hook.premiumvanish;

import de.myzelyam.api.vanish.PlayerHideEvent;
import de.myzelyam.api.vanish.PlayerShowEvent;
import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import se.filledev.procosmetics.ProCosmeticsPlugin;
import se.filledev.procosmetics.vanish.VanishHook;

// Used for both PremiumVanish and SuperVanish as they share the same API
public class PremiumVanishListener implements Listener, VanishHook {

    private final ProCosmeticsPlugin plugin;

    public PremiumVanishListener(ProCosmeticsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVanish(PlayerHideEvent event) {
        plugin.getVanishManager().hideCosmetics(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUnVanish(PlayerShowEvent event) {
        plugin.getVanishManager().updateCosmeticsLater(event.getPlayer());
    }

    @Override
    public boolean isVanished(Player player) {
        return VanishAPI.isInvisible(player);
    }
}
