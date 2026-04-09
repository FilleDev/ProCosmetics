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
package se.filledev.procosmetics.v26_1;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import se.filledev.procosmetics.nms.NMSUtilImpl;

public class NMSUtil extends NMSUtilImpl {

    @Override
    public void playChestAnimation(Block block, boolean open) {
        BlockPos blockPos = new BlockPos(block.getX(), block.getY(), block.getZ());

        ((CraftWorld) block.getWorld()).getHandle().blockEvent(blockPos,
                block.getType() == Material.CHEST ? Blocks.CHEST : Blocks.ENDER_CHEST,
                1,
                open ? 1 : 0
        );
    }
}
