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

import org.bukkit.entity.Player;
import se.filledev.procosmetics.api.cosmetic.CosmeticType;

/**
 * Decides how duplicate cosmetics rolled from a treasure chest are handled.
 * <p>
 * Only cosmetics can be duplicates. Coins, gadget ammo and custom loot are always
 * considered new loot.
 *
 * @see DuplicateHandling
 */
public interface DuplicateHandler {

    /**
     * Gets the configured duplicate handling mode.
     *
     * @return the {@link DuplicateHandling} mode
     */
    DuplicateHandling getMode();

    /**
     * Checks whether the given loot entry would be a duplicate for the player.
     *
     * @param player the player receiving the loot
     * @param entry  the rolled loot entry
     * @return {@code true} if the player already owns this loot, otherwise {@code false}
     */
    boolean isDuplicate(Player player, LootEntry entry);

    /**
     * Calculates the amount of coins a duplicate of the given cosmetic pays out.
     * <p>
     * The payout is a percentage of the cosmetic's purchase cost, clamped between a
     * configurable minimum and maximum.
     *
     * @param cosmeticType the duplicated cosmetic type
     * @return the amount of coins to pay out
     */
    int getCoinPayout(CosmeticType<?, ?> cosmeticType);
}
