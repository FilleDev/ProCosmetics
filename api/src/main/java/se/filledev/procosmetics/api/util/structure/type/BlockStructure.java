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
package se.filledev.procosmetics.api.util.structure.type;

import org.bukkit.Location;
import org.bukkit.block.Block;
import se.filledev.procosmetics.api.util.structure.Structure;

/**
 * A structure composed of actual blocks placed in the world.
 */
public interface BlockStructure extends Structure<Block> {

    /**
     * Spawns the structure at the given location with optional block break effects.
     *
     * @param location         the location to spawn the structure
     * @param blockBreakEffect whether to play block break particle effects when spawning
     * @return the angle used for rotation during spawning
     */
    double spawn(Location location, boolean blockBreakEffect);
}
