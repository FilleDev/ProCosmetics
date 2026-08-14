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
package se.filledev.procosmetics.treasure.loot;

import org.bukkit.entity.Player;
import se.filledev.procosmetics.api.config.Config;
import se.filledev.procosmetics.api.cosmetic.CosmeticType;
import se.filledev.procosmetics.api.treasure.loot.CosmeticLoot;
import se.filledev.procosmetics.api.treasure.loot.DuplicateHandler;
import se.filledev.procosmetics.api.treasure.loot.DuplicateHandling;
import se.filledev.procosmetics.api.treasure.loot.LootEntry;
import se.filledev.procosmetics.util.EnumUtil;

public class DuplicateHandlerImpl implements DuplicateHandler {

    private final DuplicateHandling mode;
    private final double percentage;
    private final int minimumAmount;
    private final int maximumAmount;

    public DuplicateHandlerImpl(Config config) {
        String path = "convert_to_coins.";

        this.mode = EnumUtil.getType(DuplicateHandling.class, config.getString("duplicate_handling"));
        this.percentage = Math.max(0.0d, config.getDouble(path + "percentage"));
        this.minimumAmount = Math.max(0, config.getInt(path + "minimum_amount"));
        this.maximumAmount = Math.max(minimumAmount, config.getInt(path + "maximum_amount"));
    }

    @Override
    public DuplicateHandling getMode() {
        return mode;
    }

    @Override
    public boolean isDuplicate(Player player, LootEntry entry) {
        // Coins, gadget ammo and custom loot can never be a duplicate
        return entry instanceof CosmeticLoot cosmeticLoot
                && cosmeticLoot.getCosmeticType().hasPermission(player);
    }

    @Override
    public int getCoinPayout(CosmeticType<?, ?> cosmeticType) {
        int payout = (int) Math.round(Math.max(0, cosmeticType.getCost()) * percentage / 100.0d);

        return Math.clamp(payout, minimumAmount, maximumAmount);
    }
}
