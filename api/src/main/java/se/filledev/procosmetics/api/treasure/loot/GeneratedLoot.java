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
package se.filledev.procosmetics.api.treasure.loot;

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.filledev.procosmetics.api.cosmetic.CosmeticRarity;
import se.filledev.procosmetics.api.user.User;
import se.filledev.procosmetics.api.util.ResolvableName;

/**
 * Represents a generated instance of loot from a {@link LootEntry}.
 *
 * @see LootEntry
 */
public interface GeneratedLoot extends ResolvableName {

    /**
     * Gets the loot entry template that generated this loot.
     *
     * @return the source loot entry
     */
    LootEntry getEntry();

    /**
     * Gets the generated amount for this loot.
     *
     * @return the amount (e.g., number of coins, ammo count, or 1 for cosmetics)
     */
    int getAmount();

    /**
     * Gets the ItemStack representing this loot.
     *
     * @return the itemStack
     */
    ItemStack getItemStack();

    /**
     * Gets the rarity of this loot.
     *
     * @return the rarity
     */
    CosmeticRarity getRarity();

    /**
     * Gets the category this loot belongs to.
     *
     * @return the loot category
     */
    LootCategory getCategory();

    /**
     * Gets the translation key used for the hologram shown above the opened chest.
     *
     * @return the hologram translation key
     */
    default String getHologramTranslationKey() {
        return "treasure_chest.open.hologram";
    }

    /**
     * Gets additional resolvers for the hologram shown above the opened chest.
     * <p>
     * These are applied on top of the resolvers the animation always provides
     * ({@code loot}, {@code category} and the rarity resolvers) and must not
     * redefine them.
     *
     * @param user the user the hologram is rendered for
     * @return the additional {@link TagResolver}, empty by default
     */
    default TagResolver getHologramResolvers(User user) {
        return TagResolver.empty();
    }

    /**
     * Gives this generated loot to the player.
     *
     * @param player the player to receive the loot
     * @param user   the user representation of the player
     */
    void give(Player player, User user);
}
