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
package se.filledev.procosmetics.cosmetic.gadget.type;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import se.filledev.procosmetics.api.cosmetic.CosmeticContext;
import se.filledev.procosmetics.api.cosmetic.gadget.GadgetBehavior;
import se.filledev.procosmetics.api.cosmetic.gadget.GadgetType;
import se.filledev.procosmetics.api.nms.EntityTracker;
import se.filledev.procosmetics.api.nms.NMSEntity;
import se.filledev.procosmetics.api.util.structure.type.BlockStructure;
import se.filledev.procosmetics.nms.EntityTrackerImpl;
import se.filledev.procosmetics.util.FastMathUtil;
import se.filledev.procosmetics.util.MathUtil;
import se.filledev.procosmetics.util.MetadataUtil;
import se.filledev.procosmetics.util.RGBFade;
import se.filledev.procosmetics.util.structure.type.BlockStructureImpl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MerryGoRound implements GadgetBehavior, Listener {

    private static final ItemStack SADDLE_ITEM = new ItemStack(Material.SADDLE);
    private static final List<Horse.Color> HORSE_COLORS = List.of(Horse.Color.values());
    private static final int HORSES = 7;
    private static final double RANGE = 4.4d;
    private static final double ANGLE_PER_HORSE = 360.0d / HORSES;
    private static final double LEASH_Y_OFFSET = 6.0d;
    private static final float MAX_SPEED = 4.0f;
    private static final float ACCELERATION = 0.02f;

    private BlockStructure structure;
    private float tick;
    private final EntityTracker tracker = new EntityTrackerImpl();
    private final Map<Horse, CoasterHorse> coasterHorses = new LinkedHashMap<>();
    private final RGBFade rgbFade = new RGBFade();
    private Location center;
    private float speed;

    @Override
    public void onEquip(CosmeticContext<GadgetType> context) {
        if (structure == null) {
            structure = new BlockStructureImpl(context.getType().getStructure());
        }
    }

    @Override
    public InteractionResult onInteract(CosmeticContext<GadgetType> context, Action action, @Nullable Block clickedBlock, @Nullable Vector clickedPosition) {
        Player player = context.getPlayer();
        center = player.getLocation().getBlock().getLocation().add(0.5d, 0.0d, 0.5d);
        World world = player.getWorld();
        speed = 0.0f;

        structure.spawn(center);

        for (int i = 0; i < HORSES; i++) {
            Location location = getHorseLocation(i);
            Location leashLoc = location.clone().add(0.0d, LEASH_Y_OFFSET, 0.0d);
            location.setY(getYOffset(i));

            ItemDisplay itemDisplay = location.getWorld().spawn(location, ItemDisplay.class, entity -> {
                entity.setGravity(false);
                entity.setTeleportDuration(2);
                MetadataUtil.setCustomEntity(entity);
            });

            // Note: The preferred approach would be to use PlayerInteractUnknownEntityEvent and keep the horses client-sided.
            // Since this event is not available in Spigot (Paper-only), the horses must remain server-sided for now.
            int finalI = i;
            Horse horse = location.getWorld().spawn(location, Horse.class, entity -> {
                entity.setColor(HORSE_COLORS.get(finalI % HORSE_COLORS.size()));
                entity.setAI(false);
                entity.setInvulnerable(true);
                entity.getInventory().setSaddle(SADDLE_ITEM);
                MetadataUtil.setCustomEntity(entity);
            });
            horse.addPassenger(itemDisplay);
            NMSEntity nmsEntityHorse = context.getPlugin().getNMSManager().entityToNMSEntity(horse);
            NMSEntity nmsEntityItemDisplay = context.getPlugin().getNMSManager().entityToNMSEntity(itemDisplay);

            NMSEntity nmsEntityLeash = context.getPlugin().getNMSManager().createEntity(world, EntityType.BAT, tracker);
            nmsEntityLeash.setPositionRotation(leashLoc);
            nmsEntityLeash.setLeashHolder(nmsEntityHorse.getBukkitEntity());
            if (nmsEntityLeash.getBukkitEntity() instanceof LivingEntity livingEntity) {
                livingEntity.setInvisible(true);
            }
            CoasterHorse coasterHorse = new CoasterHorse(nmsEntityHorse, nmsEntityItemDisplay, nmsEntityLeash);
            coasterHorses.put(horse, coasterHorse);
        }
        tracker.startTracking();

        List<Player> nearbyPlayers = MathUtil.getClosestPlayersFromLocation(center, 6.0d);

        if (!nearbyPlayers.isEmpty()) {
            Location teleport = center.clone().add(4.5d, 1.5d, 0.0d);
            teleport.setDirection(player.getLocation().getDirection());

            for (Player closePlayer : nearbyPlayers) {
                closePlayer.teleport(teleport);
            }
        }
        context.getPlugin().getJavaPlugin().getServer().getScheduler().runTaskLater(context.getPlugin().getJavaPlugin(),
                () -> onUnequip(context),
                context.getType().getDurationTicks()
        );
        return InteractionResult.success();
    }

    private Location getHorseLocation(float angle) {
        Location location = MathUtil.getLocationAroundCircle(center, RANGE, angle);
        location.setYaw(location.getYaw() - 90.0f);
        return location;
    }

    private double getYOffset(float angle) {
        double y = FastMathUtil.sin(angle * 2.0f) + 2.0d;
        return center.getY() + y;
    }

    @Override
    public void onUpdate(CosmeticContext<GadgetType> context) {
        if (center == null) {
            return;
        }
        int i = 0;
        for (Map.Entry<?, CoasterHorse> entry : coasterHorses.entrySet()) {
            CoasterHorse coasterHorse = entry.getValue();
            NMSEntity nmsHorse = coasterHorse.horse();
            NMSEntity nmsLeash = coasterHorse.leash();

            float angle = FastMathUtil.toRadians(ANGLE_PER_HORSE * i + tick);
            Location location = getHorseLocation(angle);

            location.add(0.0d, LEASH_Y_OFFSET, 0.0d);
            nmsLeash.sendPositionRotationPacket(location);
            location.subtract(0.0d, LEASH_Y_OFFSET, 0.0d);

            location.setY(getYOffset(angle));
            nmsHorse.sendPositionRotationPacket(location);

            location.getWorld().spawnParticle(Particle.DUST, location.add(0.0d, 1.0d, 0.0d), 5, 0, 0, 0, 0.0d,
                    new Particle.DustOptions(org.bukkit.Color.fromRGB(rgbFade.getR(), rgbFade.getG(), rgbFade.getB()), 1)
            );
            i++;
        }

        if (speed < MAX_SPEED) {
            speed += ACCELERATION;
        }
        rgbFade.nextRGB();

        tick += speed;

        if (tick > 360) {
            tick = 0;
        }
    }

    @Override
    public void onUnequip(CosmeticContext<GadgetType> context) {
        structure.remove();

        tracker.destroy();

        for (CoasterHorse coasterHorse : coasterHorses.values()) {
            coasterHorse.display().getBukkitEntity().remove();
            coasterHorse.horse().getBukkitEntity().remove();
        }
        coasterHorses.clear();
    }

    @Override
    public boolean requiresGroundOnUse() {
        return true;
    }

    @Override
    public boolean isEnoughSpaceToUse(Location location) {
        return structure.isEnoughSpace(location);
    }

    @Override
    public boolean shouldUnequipOnTeleport() {
        return true;
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Horse horse) {
            CoasterHorse coasterHorse = coasterHorses.get(horse);

            if (coasterHorse != null) {
                Entity display = coasterHorse.display().getBukkitEntity();

                if (display.getPassengers().isEmpty()) {
                    display.addPassenger(event.getPlayer());
                }
            }
        }
    }

    public record CoasterHorse(NMSEntity horse, NMSEntity display, NMSEntity leash) {
    }
}
