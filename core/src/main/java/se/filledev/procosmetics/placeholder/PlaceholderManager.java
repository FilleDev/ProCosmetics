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
package se.filledev.procosmetics.placeholder;

import org.bukkit.entity.Player;
import se.filledev.procosmetics.ProCosmeticsPlugin;
import se.filledev.procosmetics.placeholder.incoming.PlaceholderResolverAPIResolver;
import se.filledev.procosmetics.placeholder.outgoing.PlaceholderAPIExpansion;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class PlaceholderManager {

    private final ProCosmeticsPlugin plugin;
    private final List<PlaceholderResolver> resolvers = new ArrayList<>();

    public PlaceholderManager(ProCosmeticsPlugin plugin) {
        this.plugin = plugin;
        hookPlugins();
    }

    private void hookPlugins() {
        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            plugin.getLogger().log(Level.INFO, "Hooking into PlaceholderAPI...");
            new PlaceholderAPIExpansion(plugin).register();
            register(new PlaceholderResolverAPIResolver());
        }
    }

    public void register(PlaceholderResolver placeholder) {
        resolvers.add(placeholder);
    }

    public String resolve(Player player, String text) {
        for (PlaceholderResolver placeholder : resolvers) {
            text = placeholder.resolve(player, text);
        }
        text = text.replace("<coins>", String.valueOf(plugin.getEconomyManager().getEconomyProvider().getCoins(plugin.getUserManager().getConnected(player))))
                .replace("<ping>", String.valueOf(player.getPing()));

        return text;
    }

    public List<String> resolve(Player player, List<String> text) {
        for (PlaceholderResolver resolver : resolvers) {
            text = resolver.resolve(player, text);
        }
        return text;
    }
}
