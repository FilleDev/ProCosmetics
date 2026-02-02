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
package se.filledev.procosmetics.cosmetic.morph.type;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import se.filledev.procosmetics.ProCosmeticsPlugin;
import se.filledev.procosmetics.api.cosmetic.CosmeticContext;
import se.filledev.procosmetics.api.cosmetic.morph.MorphBehavior;
import se.filledev.procosmetics.api.cosmetic.morph.MorphType;
import se.filledev.procosmetics.api.nms.NMSEntity;
import se.filledev.procosmetics.api.user.User;
import se.filledev.procosmetics.util.MathUtil;

public class IronGolem implements MorphBehavior {

    @Override
    public void onEquip(CosmeticContext<MorphType> context) {
    }

    @Override
    public void setupEntity(CosmeticContext<MorphType> context, NMSEntity nmsEntity) {
    }

    @Override
    public InteractionResult onInteract(CosmeticContext<MorphType> context, PlayerInteractEvent event, NMSEntity nmsEntity) {
        Action action = event.getAction();

        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            Player player = context.getPlayer();
            Location location = player.getLocation();

            Location attackLocation = location.clone().add(location.getDirection().multiply(2.0d));
            attackLocation.setY(location.getY());

            nmsEntity.sendEntityEventPacket((byte) 4);

            // Find and push nearby players
            for (Player pushPlayer : MathUtil.getClosestPlayersFromLocation(attackLocation, 3.0d)) {
                if (pushPlayer != player) {
                    MathUtil.pushEntity(pushPlayer, location, 0.3d, 0.9d);
                    pushPlayer.getWorld().spawnParticle(Particle.CLOUD,
                            pushPlayer.getLocation().add(0.0d, 0.3d, 0.0d),
                            5,
                            0.1f,
                            0.1f,
                            0.1f,
                            0.0d
                    );
                    ProCosmeticsPlugin plugin = ProCosmeticsPlugin.getPlugin();
                    User otherUser = plugin.getUserManager().getConnected(pushPlayer);

                    if (otherUser != null) {
                        otherUser.setFallDamageProtection(4);
                    }
                }
            }
            player.getWorld().playSound(location, Sound.ENTITY_IRON_GOLEM_HURT, 0.6f, 1.2f);

            return InteractionResult.success();
        }
        return InteractionResult.noAction();
    }

    @Override
    public void onUpdate(CosmeticContext<MorphType> context, NMSEntity nmsEntity) {
    }

    @Override
    public void onUnequip(CosmeticContext<MorphType> context) {
    }
}
