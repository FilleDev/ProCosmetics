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
package se.filledev.procosmetics.cosmetic.mount;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import se.filledev.procosmetics.ProCosmeticsPlugin;
import se.filledev.procosmetics.api.cosmetic.CosmeticRarity;
import se.filledev.procosmetics.api.cosmetic.mount.Mount;
import se.filledev.procosmetics.api.cosmetic.mount.MountBehavior;
import se.filledev.procosmetics.api.cosmetic.mount.MountType;
import se.filledev.procosmetics.api.cosmetic.registry.CosmeticCategory;
import se.filledev.procosmetics.api.user.User;
import se.filledev.procosmetics.cosmetic.CosmeticTypeImpl;

import java.util.List;
import java.util.function.Supplier;

public class MountTypeImpl extends CosmeticTypeImpl<MountType, MountBehavior> implements MountType {

    private final EntityType entityType;

    public MountTypeImpl(String key,
                         CosmeticCategory<MountType, MountBehavior, ?> category,
                         Supplier<MountBehavior> behaviorFactory,
                         boolean enabled,
                         boolean purchasable,
                         int cost,
                         CosmeticRarity rarity,
                         ItemStack itemStack,
                         List<String> treasureChests,
                         EntityType entityType) {
        super(key, category, behaviorFactory, enabled, purchasable, cost, rarity, itemStack, treasureChests);
        this.entityType = entityType;
    }

    @Override
    protected Mount createInstance(ProCosmeticsPlugin plugin, User user, MountBehavior behavior) {
        return new MountImpl(plugin, user, this, behavior);
    }

    @Override
    public EntityType getEntityType() {
        return entityType;
    }

    public static class BuilderImpl extends CosmeticTypeImpl.BuilderImpl<MountType, MountBehavior, MountType.Builder> implements MountType.Builder {

        private EntityType entityType;

        public BuilderImpl(String key, CosmeticCategory<MountType, MountBehavior, ?> category) {
            super(key, category);
        }

        @Override
        protected MountType.Builder self() {
            return this;
        }

        public MountType.Builder entityType(EntityType entityType) {
            this.entityType = entityType;
            return this;
        }

        @Override
        public MountType build() {
            return new MountTypeImpl(key,
                    category,
                    factory,
                    enabled,
                    purchasable,
                    cost,
                    rarity,
                    itemStack,
                    treasureChests,
                    entityType
            );
        }
    }
}
