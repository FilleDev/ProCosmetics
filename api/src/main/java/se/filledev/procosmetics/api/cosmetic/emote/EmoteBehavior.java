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
package se.filledev.procosmetics.api.cosmetic.emote;

import se.filledev.procosmetics.api.cosmetic.CosmeticBehavior;
import se.filledev.procosmetics.api.cosmetic.CosmeticContext;

/**
 * Defines the behavior for emote cosmetics.
 *
 * @see CosmeticBehavior
 * @see EmoteType
 */
public interface EmoteBehavior extends CosmeticBehavior<EmoteType> {

    /**
     * Called every tick to update the emote animation.
     *
     * @param context the context containing information about the emote,
     *                including the player and cosmetic type
     * @param frame   the current animation frame number
     */
    void onUpdate(CosmeticContext<EmoteType> context, int frame);
}
