/*
 * This file is part of ProCosmetics - https://github.com/FilleDev/ProCosmetics
 * Copyright (C) 2026 FilleDev and contributors
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
package se.filledev.procosmetics.api.platform;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PlatformAdapter {

    /**
     * Gets an {@link Audience} for the given player.
     *
     * @param player the player
     * @return the audience
     */
    Audience audience(Player player);

    /**
     * Gets an {@link Audience} for the given command sender.
     *
     * @param commandSender the command sender
     * @return the audience
     */
    Audience audience(CommandSender commandSender);

    /**
     * Sets the text of a text display entity.
     *
     * @param textDisplay the text display to set the text on
     * @param component   the text to set
     */
    void setText(TextDisplay textDisplay, Component component);

    /**
     * Sets the custom name of an entity.
     *
     * @param entity    the entity to set the name on
     * @param component the name to set
     */
    void setCustomName(Entity entity, Component component);

    /**
     * Gets the display name of an item.
     *
     * @param itemMeta the item meta to get the name from
     * @return the display name, or null if not set
     */
    @Nullable
    Component getDisplayName(ItemMeta itemMeta);

    /**
     * Sets the display name of an item.
     *
     * @param itemMeta  the item meta to set the name on
     * @param component the name to set
     */
    void setDisplayName(ItemMeta itemMeta, Component component);

    /**
     * Gets the lore of an item.
     *
     * @param itemMeta the item meta to get the lore from
     * @return the lore lines, or null if none are set
     */
    @Nullable
    List<Component> getLore(ItemMeta itemMeta);

    /**
     * Sets the lore of an item.
     *
     * @param itemMeta   the item meta to set the lore on
     * @param components the lore lines to set
     */
    void setLore(ItemMeta itemMeta, List<Component> components);

    /**
     * Creates an inventory for the given player.
     *
     * @param player the player to create the inventory for
     * @param rows   the number of rows in the inventory
     * @param title  the title of the inventory
     * @return the created inventory
     */
    Inventory createInventory(Player player, int rows, Component title);
}
