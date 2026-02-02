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
package se.filledev.procosmetics.cosmetic.miniature.type;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import se.filledev.procosmetics.api.cosmetic.CosmeticContext;
import se.filledev.procosmetics.api.cosmetic.miniature.MiniatureBehavior;
import se.filledev.procosmetics.api.cosmetic.miniature.MiniatureType;
import se.filledev.procosmetics.api.nms.NMSEntity;
import se.filledev.procosmetics.util.MathUtil;
import se.filledev.procosmetics.util.item.ItemBuilderImpl;

public class Devil implements MiniatureBehavior {

    private static final ItemStack HAND_ITEM = new ItemStack(Material.BLAZE_POWDER);
    private static final ItemStack CHESTPLATE_ITEM;
    private static final ItemStack LEGGINGS_ITEM;
    private static final ItemStack BOOTS_ITEM;

    static {
        Color color = Color.RED;
        CHESTPLATE_ITEM = new ItemBuilderImpl(Material.LEATHER_CHESTPLATE).setLeatherArmorColor(color).getItemStack();
        LEGGINGS_ITEM = new ItemBuilderImpl(Material.LEATHER_LEGGINGS).setLeatherArmorColor(color).getItemStack();
        BOOTS_ITEM = new ItemBuilderImpl(Material.LEATHER_BOOTS).setLeatherArmorColor(color).getItemStack();
    }

    @Override
    public void onEquip(CosmeticContext<MiniatureType> context) {
    }

    @Override
    public void setupEntity(CosmeticContext<MiniatureType> context, NMSEntity nmsEntity) {
        nmsEntity.setMainHand(HAND_ITEM);
        nmsEntity.setChestplate(CHESTPLATE_ITEM);
        nmsEntity.setLeggings(LEGGINGS_ITEM);
        nmsEntity.setBoots(BOOTS_ITEM);
    }

    @Override
    public void onUpdate(CosmeticContext<MiniatureType> context, NMSEntity nmsEntity, int tick) {
        if (tick % 5 == 0) {
            nmsEntity.getPreviousLocation().getWorld().spawnParticle(Particle.LAVA,
                    nmsEntity.getPreviousLocation(),
                    1,
                    0.1d,
                    0.0d,
                    0.1d,
                    0.0d
            );
        }

        if (tick % 15 == 0) {
            MathUtil.findLocationsInCircle(nmsEntity.getPreviousLocation(), 8, 0.3d, 1.5d,
                    location -> location.getWorld().spawnParticle(Particle.FLAME,
                            location,
                            1,
                            0.0d,
                            0.0d,
                            0.0d,
                            0.0d
                    )
            );
        }
    }

    @Override
    public void onUnequip(CosmeticContext<MiniatureType> context) {
    }
}
