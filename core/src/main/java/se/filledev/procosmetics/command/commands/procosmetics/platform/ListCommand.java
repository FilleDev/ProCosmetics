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
package se.filledev.procosmetics.command.commands.procosmetics.platform;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import se.filledev.procosmetics.ProCosmeticsPlugin;
import se.filledev.procosmetics.api.locale.Translator;
import se.filledev.procosmetics.api.treasure.TreasureChestPlatform;
import se.filledev.procosmetics.command.SubCommand;

import java.util.Comparator;
import java.util.List;

public class ListCommand extends SubCommand<CommandSender> {

    public ListCommand(ProCosmeticsPlugin plugin) {
        super(plugin, "procosmetics.command.platform.list", true);
        addFlats("platform", "list");
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Translator translator = translator(sender);

        audience(sender).sendMessage(translator.translate(
                "command.platform.list.header",
                Placeholder.unparsed("amount", String.valueOf(plugin.getTreasureChestManager().getPlatforms().size()))
        ));
        TextComponent.Builder builder = Component.text();
        List<TreasureChestPlatform> sortedPlatforms = plugin.getTreasureChestManager().getPlatforms()
                .stream()
                .sorted(Comparator.comparingInt(TreasureChestPlatform::getId))
                .toList();

        for (int i = 0; i < sortedPlatforms.size(); i++) {
            TreasureChestPlatform platform = sortedPlatforms.get(i);
            Location center = platform.getCenter();

            builder.append(translator.translate(
                    "command.platform.list.entry",
                    Placeholder.unparsed("id", String.valueOf(platform.getId())),
                    Placeholder.unparsed("x", String.valueOf(center.getBlockX())),
                    Placeholder.unparsed("y", String.valueOf(center.getBlockY())),
                    Placeholder.unparsed("z", String.valueOf(center.getBlockZ())),
                    Placeholder.unparsed("world", center.getWorld().getName())
            ).clickEvent(ClickEvent.runCommand("/pc platform teleport " + platform.getId())));

            if (i < sortedPlatforms.size() - 1) {
                builder.append(Component.newline());
            }
        }
        audience(sender).sendMessage(builder);
    }
}
