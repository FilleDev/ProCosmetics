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
package se.filledev.procosmetics.treasure.loot.type;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.filledev.procosmetics.api.cosmetic.CosmeticRarity;
import se.filledev.procosmetics.api.cosmetic.CosmeticType;
import se.filledev.procosmetics.api.treasure.loot.CosmeticLoot;
import se.filledev.procosmetics.api.treasure.loot.GeneratedLoot;
import se.filledev.procosmetics.api.treasure.loot.LootCategory;
import se.filledev.procosmetics.api.treasure.loot.number.ConstantIntProvider;
import se.filledev.procosmetics.api.user.User;
import se.filledev.procosmetics.menu.menus.purchase.CosmeticPurchaseMenu;
import se.filledev.procosmetics.treasure.loot.GeneratedLootImpl;
import se.filledev.procosmetics.treasure.loot.LootEntryImpl;

public class CosmeticLootImpl extends LootEntryImpl<ConstantIntProvider> implements CosmeticLoot {

    private final CosmeticType<?, ?> cosmeticType;

    public CosmeticLootImpl(ConstantIntProvider intProvider, LootCategory category, CosmeticType<?, ?> cosmeticType) {
        super(intProvider, category);
        this.cosmeticType = cosmeticType;
    }

    @Override
    public String getKey() {
        return cosmeticType.getKey();
    }

    @Override
    public String getNameTranslationKey() {
        return cosmeticType.getNameTranslationKey();
    }

    @Override
    public GeneratedLoot generate() {
        return new GeneratedCosmeticLoot(this, intProvider.get());
    }

    @Override
    public CosmeticRarity getRarity() {
        return cosmeticType.getRarity();
    }

    @Override
    public ItemStack getItemStack() {
        return cosmeticType.getItemStack();
    }

    @Override
    public CosmeticType<?, ?> getCosmeticType() {
        return cosmeticType;
    }

    private static class GeneratedCosmeticLoot extends GeneratedLootImpl<CosmeticLoot> {

        public GeneratedCosmeticLoot(CosmeticLoot entry, int amount) {
            super(entry, amount);
        }

        @Override
        public void give(Player player, User user) {
            CosmeticType<?, ?> cosmeticType = entry.getCosmeticType();
            CosmeticRarity rarity = cosmeticType.getRarity();
            CosmeticPurchaseMenu.grantCosmeticPermission(PLUGIN, player, cosmeticType);

            PLUGIN.getTreasureChestManager().getLootBroadcaster().broadcastMessage(
                    player,
                    rarity,
                    "treasure_chest.loot." + getCategory().getKey() + ".broadcast",
                    receiverUser -> TagResolver.resolver(
                            Placeholder.unparsed("player", player.getName()),
                            getResolvers(receiverUser)
                    ));
        }
    }
}
