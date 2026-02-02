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
package se.filledev.procosmetics.command.commands.procosmetics.add;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.filledev.procosmetics.ProCosmeticsPlugin;
import se.filledev.procosmetics.api.cosmetic.CosmeticType;
import se.filledev.procosmetics.api.cosmetic.registry.CosmeticCategory;
import se.filledev.procosmetics.api.locale.Translator;
import se.filledev.procosmetics.api.user.User;
import se.filledev.procosmetics.command.SubCommand;
import se.filledev.procosmetics.menu.menus.purchase.CosmeticPurchaseMenu;

import java.util.stream.Collectors;

public class AddCosmeticCommand extends SubCommand<CommandSender> {

    public AddCosmeticCommand(ProCosmeticsPlugin plugin) {
        super(plugin, "procosmetics.command.add.cosmetic", true);
        addFlats("add", "cosmetic");
        addArgument(Player.class, "target", sender -> plugin.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        addArgument(String.class, "category", sender -> plugin.getCategoryRegistries().getCategories().stream().map(CosmeticCategory::getKey).collect(Collectors.toList()));
        addArgument(String.class, "cosmetic");
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Translator translator = translator(sender);
        Player target = parseArgument(args, 2);

        if (target == null) {
            audience(sender).sendMessage(translator.translate("generic.player_offline"));
            return;
        }
        User user = plugin.getUserManager().getConnected(target);

        if (user == null) {
            audience(sender).sendMessage(translator.translate("generic.error.player_data.target"));
            return;
        }
        CosmeticCategory<?, ?, ?> category = plugin.getCategoryRegistries().getCategory(parseArgument(args, 3));

        if (category == null) {
            audience(sender).sendMessage(translator.translate("cosmetic.category_not_found"));
            return;
        }
        CosmeticType<?, ?> cosmeticType = category.getCosmeticRegistry().getType(parseArgument(args, 4));

        if (cosmeticType == null) {
            audience(sender).sendMessage(translator.translate("cosmetic.not_found"));
            return;
        }

        if (target.hasPermission(cosmeticType.getPermission())) {
            audience(sender).sendMessage(translator.translate(
                    "command.cosmetic.add.already_owned",
                    Placeholder.unparsed("player", target.getName())
            ));
            return;
        }
        CosmeticPurchaseMenu.grantCosmeticPermission(plugin, target, cosmeticType);

        audience(sender).sendMessage(translator.translate(
                "command.add.cosmetic",
                Placeholder.unparsed("cosmetic", cosmeticType.getName(translator)),
                Placeholder.unparsed("player", target.getName())
        ));
    }
}
