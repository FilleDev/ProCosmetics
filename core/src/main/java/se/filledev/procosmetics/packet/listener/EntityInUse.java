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
package se.filledev.procosmetics.packet.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import se.filledev.procosmetics.ProCosmeticsPlugin;
import se.filledev.procosmetics.cosmetic.gadget.type.MerryGoRound;
import se.filledev.procosmetics.packet.PacketHandler;
import se.filledev.procosmetics.util.ReflectionUtil;
import se.filledev.procosmetics.util.mapping.MappingRegistry;

import java.lang.reflect.Field;
import java.util.logging.Level;

public class EntityInUse extends PacketHandler {

    private final Field getIDField;

    public EntityInUse(ProCosmeticsPlugin plugin) {
        super(plugin, "network.protocol.game", MappingRegistry.getMappedFieldName(MappingRegistry.SERVERBOUND_INTERACT_PACKET));
        getIDField = ReflectionUtil.getDeclaredField(clazz, MappingRegistry.getMappedFieldName(MappingRegistry.SERVERBOUND_INTERACT_PACKET_ID));
    }

    @Override
    public void onPacket(Player player, Object packet) {
        try {
            int id = (int) getIDField.get(packet);

            for (MerryGoRound.CoasterHorse coasterHorse : MerryGoRound.COASTER_HORSES) {
                if (coasterHorse.horse().getId() == id) {
                    Entity entity = coasterHorse.armorStand().getBukkitEntity();

                    if (entity.getPassengers().isEmpty()) {
                        plugin.getServer().getScheduler().runTask(plugin, () -> entity.addPassenger(player));
                    }
                    return;
                }
            }
        } catch (IllegalAccessException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to get the entity id in EntityInUse listener!", e);
        }
    }
}
