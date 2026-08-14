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
package se.filledev.procosmetics.treasure.loot.type;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.filledev.procosmetics.api.cosmetic.CosmeticRarity;
import se.filledev.procosmetics.api.cosmetic.CosmeticType;
import se.filledev.procosmetics.api.treasure.loot.CosmeticLoot;
import se.filledev.procosmetics.api.treasure.loot.DuplicateHandler;
import se.filledev.procosmetics.api.treasure.loot.DuplicateHandling;
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
    public GeneratedLoot generate(Player player) {
        DuplicateHandler duplicateHandler = PLUGIN.getTreasureChestManager().getDuplicateHandler();

        // PREVENT only ends up here when the player owns every cosmetic in the chest,
        // in which case a coin payout is the only sensible thing left to give.
        if (duplicateHandler.getMode() != DuplicateHandling.ALLOW && duplicateHandler.isDuplicate(player, this)) {
            return new GeneratedDuplicateCosmeticLoot(this, duplicateHandler.getCoinPayout(cosmeticType));
        }
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

    /**
     * A cosmetic the player already owns, paid out as coins instead.
     * <p>
     * The rolled cosmetic is still shown in the animation, but the reward is the
     * coin payout carried by {@link #getAmount()}.
     */
    private static class GeneratedDuplicateCosmeticLoot extends GeneratedLootImpl<CosmeticLoot> {

        public GeneratedDuplicateCosmeticLoot(CosmeticLoot entry, int coins) {
            super(entry, coins);
        }

        @Override
        public TagResolver getResolvers(User user) {
            return TagResolver.resolver(
                    super.getResolvers(user),
                    Placeholder.unparsed("currency", user.translateRaw("generic.currency"))
            );
        }

        @Override
        public String getHologramTranslationKey() {
            return "treasure_chest.open.hologram_duplicate";
        }

        @Override
        public TagResolver getHologramResolvers(User user) {
            return TagResolver.resolver(
                    Placeholder.unparsed("amount", String.valueOf(amount)),
                    Placeholder.unparsed("currency", user.translateRaw("generic.currency"))
            );
        }

        @Override
        public void give(Player player, User user) {
            PLUGIN.getEconomyManager().getEconomyProvider().addCoinsAsync(user, amount).thenAcceptAsync(result -> {
                if (result.booleanValue()) {
                    PLUGIN.getTreasureChestManager().getLootBroadcaster().broadcastMessage(
                            player,
                            entry.getRarity(),
                            "treasure_chest.loot.duplicate.broadcast",
                            receiverUser -> TagResolver.resolver(
                                    Placeholder.unparsed("player", player.getName()),
                                    getResolvers(receiverUser)
                            ));
                } else {
                    user.sendMessage(user.translate("generic.error.database"));
                }
            }, PLUGIN.getSyncExecutor());
        }
    }
}
