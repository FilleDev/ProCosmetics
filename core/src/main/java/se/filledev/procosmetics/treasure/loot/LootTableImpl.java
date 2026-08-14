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
import org.jetbrains.annotations.Nullable;
import se.filledev.procosmetics.ProCosmeticsPlugin;
import se.filledev.procosmetics.api.cosmetic.CosmeticRarity;
import se.filledev.procosmetics.api.treasure.loot.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LootTableImpl implements LootTable {

    protected static final ProCosmeticsPlugin PLUGIN = ProCosmeticsPlugin.getPlugin();
    protected static final Random RANDOM = new Random();

    private final List<LootEntry> entries;
    private final Map<CosmeticRarity, Integer> rarityWeights;
    private final int totalWeight;
    private final Map<LootCategory, List<LootEntry>> entriesByCategory;
    private final Map<CosmeticRarity, List<LootEntry>> entriesByRarity;

    public LootTableImpl(List<LootEntry> entries, Map<CosmeticRarity, Integer> rarityWeights) {
        this.entries = new ArrayList<>(entries);
        this.rarityWeights = new HashMap<>(rarityWeights);
        this.totalWeight = rarityWeights.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        this.entriesByCategory = entries.stream()
                .collect(Collectors.groupingBy(
                        LootEntry::getCategory,
                        HashMap::new,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                List::copyOf
                        )
                ));

        this.entriesByRarity = entries.stream()
                .collect(Collectors.groupingBy(
                        LootEntry::getRarity,
                        HashMap::new,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                List::copyOf
                        )
                ));
    }

    @Override
    @Nullable
    public LootEntry rollLoot(@Nullable Player player) {
        if (player != null) {
            DuplicateHandler duplicateHandler = PLUGIN.getTreasureChestManager().getDuplicateHandler();

            if (duplicateHandler.getMode() == DuplicateHandling.PREVENT) {
                LootEntry entry = roll(lootEntry -> !duplicateHandler.isDuplicate(player, lootEntry));

                if (entry != null) {
                    return entry;
                }
                // The player owns everything this chest has to offer. Fall through to a normal
                // roll and let the duplicate be paid out as coins instead of giving nothing.
            }
        }
        return roll(null);
    }

    /**
     * Rolls a rarity among those that still have loot matching the filter, then picks a
     * random matching entry from it. Rarities without any matching entry are skipped, so
     * the relative weights of the remaining rarities are kept intact.
     *
     * @param filter the filter the loot has to match, or {@code null} to roll the whole table
     */
    @Nullable
    private LootEntry roll(@Nullable Predicate<LootEntry> filter) {
        Map<CosmeticRarity, List<LootEntry>> availableByRarity = new HashMap<>();
        int availableWeight = 0;

        for (Map.Entry<CosmeticRarity, Integer> entry : rarityWeights.entrySet()) {
            List<LootEntry> rarityEntries = entriesByRarity.get(entry.getKey());

            if (rarityEntries == null) {
                continue;
            }
            List<LootEntry> availableEntries = filter == null
                    ? rarityEntries
                    : rarityEntries.stream().filter(filter).toList();

            if (availableEntries.isEmpty()) {
                continue;
            }
            availableByRarity.put(entry.getKey(), availableEntries);
            availableWeight += entry.getValue();
        }
        if (availableWeight <= 0) {
            return null;
        }
        int randomValue = RANDOM.nextInt(availableWeight);
        int currentWeight = 0;

        for (Map.Entry<CosmeticRarity, List<LootEntry>> entry : availableByRarity.entrySet()) {
            currentWeight += rarityWeights.get(entry.getKey());

            if (randomValue < currentWeight) {
                List<LootEntry> availableEntries = entry.getValue();
                return availableEntries.get(RANDOM.nextInt(availableEntries.size()));
            }
        }
        return null;
    }

    @Override
    public double getEntryChance(LootEntry entry) {
        List<LootEntry> rarityEntries = entriesByRarity.get(entry.getRarity());

        if (rarityEntries == null || rarityEntries.isEmpty()) {
            return 0.0d;
        }
        return getRarityChance(entry.getRarity()) / rarityEntries.size();
    }

    @Override
    public double getRarityChance(CosmeticRarity rarity) {
        Integer weight = rarityWeights.get(rarity);

        if (weight == null || totalWeight == 0) {
            return 0.0d;
        }
        return ((double) weight / totalWeight) * 100.0d;
    }

    @Override
    public List<LootEntry> getEntries() {
        return entries;
    }

    @Override
    public Map<LootCategory, List<LootEntry>> getEntriesByCategory() {
        return entriesByCategory;
    }

    @Override
    public int getTotalWeight() {
        return totalWeight;
    }
}
