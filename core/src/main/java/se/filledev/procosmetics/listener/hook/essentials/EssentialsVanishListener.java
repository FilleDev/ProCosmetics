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
package se.filledev.procosmetics.listener.hook.essentials;

import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import net.ess3.api.events.VanishStatusChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import se.filledev.procosmetics.ProCosmeticsPlugin;
import se.filledev.procosmetics.vanish.VanishHook;

public class EssentialsVanishListener implements Listener, VanishHook {

    private final ProCosmeticsPlugin plugin;
    private final IEssentials essentials;

    public EssentialsVanishListener(ProCosmeticsPlugin plugin) {
        this.plugin = plugin;
        this.essentials = (IEssentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVanishStatusChange(VanishStatusChangeEvent event) {
        Player player = event.getAffected().getBase().getPlayer();

        if (player == null) {
            return;
        }

        if (event.getValue()) {
            plugin.getVanishManager().hideCosmetics(player);
        } else {
            plugin.getVanishManager().updateCosmeticsLater(player);
        }
    }

    @Override
    public boolean isVanished(Player player) {
        User user = essentials.getUser(player);
        return user != null && user.isVanished();
    }
}
