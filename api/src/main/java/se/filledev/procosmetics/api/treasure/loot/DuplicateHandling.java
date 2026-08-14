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

/**
 * Defines what happens when a treasure chest rolls a cosmetic the player already owns.
 *
 * @see DuplicateHandler
 */
public enum DuplicateHandling {

    /**
     * Gives the duplicate anyway. The roll is effectively wasted.
     */
    ALLOW,

    /**
     * Pays out coins instead of the duplicate cosmetic.
     *
     * @see DuplicateHandler#getCoinPayout(se.filledev.procosmetics.api.cosmetic.CosmeticType)
     */
    CONVERT_TO_COINS,

    /**
     * Excludes owned cosmetics from the roll so a duplicate can never be rolled.
     * <p>
     * When every entry of a rarity is already owned, another rarity is picked instead.
     * When the player owns everything in the chest, the roll falls back to
     * {@link #CONVERT_TO_COINS}.
     */
    PREVENT
}
