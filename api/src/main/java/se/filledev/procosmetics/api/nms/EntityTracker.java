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
package se.filledev.procosmetics.api.nms;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Manages visibility and lifecycle of a group of NMS entities.
 * Entity trackers automatically handle spawning and despawning entities for players
 * based on configurable range, owner restrictions, anti-viewer lists, and custom
 * visibility predicates. Trackers run periodic updates to maintain viewer lists
 * and can be configured with callbacks for viewer and entity lifecycle events.
 *
 * <p>Typical usage involves creating a tracker via the {@link Builder}, adding
 * entities, and calling {@link #startTracking()} to begin automatic viewer management.</p>
 */
public interface EntityTracker {

    /**
     * Adds an entity to this tracker.
     *
     * @param nmsEntity the entity to add
     */
    void addEntity(NMSEntity nmsEntity);

    /**
     * Removes an entity from this tracker.
     *
     * @param nmsEntity the entity to remove
     */
    void removeEntity(NMSEntity nmsEntity);

    /**
     * Gets all entities managed by this tracker.
     *
     * @return immutable collection of entities
     */
    Collection<NMSEntity> getEntities();

    /**
     * Removes all entities from this tracker.
     */
    void clearEntities();

    /**
     * Starts tracking and spawning entities for nearby players.
     */
    void startTracking();

    /**
     * Stops tracking and despawns entities for all viewers.
     */
    void stopTracking();

    /**
     * Checks if tracking is currently active.
     *
     * @return true if tracking is active
     */
    boolean isTracking();

    /**
     * Destroys this tracker and cleans up all resources.
     */
    void destroy();

    /**
     * Gets all players currently viewing entities from this tracker.
     *
     * @return immutable collection of viewing players
     */
    Collection<Player> getViewers();

    /**
     * Manually adds a viewer to this tracker.
     *
     * @param player the player to add as a viewer
     */
    void addViewer(Player player);

    /**
     * Adds multiple viewers to this tracker at once.
     *
     * @param player the collection of players to add as viewers
     */
    @ApiStatus.Internal
    void addViewers(Collection<Player> player);

    /**
     * Manually removes a viewer from this tracker.
     *
     * @param player the player to remove as a viewer
     */
    void removeViewer(Player player);

    /**
     * Removes multiple viewers from this tracker at once.
     *
     * @param player the collection of players to remove as viewers
     */
    @ApiStatus.Internal
    void removeViewers(Collection<Player> player);

    /**
     * Checks if a player is currently viewing entities from this tracker.
     *
     * @param player the player to check
     * @return true if the player is a viewer
     */
    boolean isViewer(Player player);

    /**
     * Adds a player to the anti-viewer list (prevents them from seeing entities).
     *
     * @param player the player to add to the anti-viewer list
     */
    void addAntiViewer(Player player);

    /**
     * Removes a player from the anti-viewer list.
     *
     * @param player the player to remove from the anti-viewer list
     */
    void removeAntiViewer(Player player);

    /**
     * Checks if a player is in the anti-viewer list.
     *
     * @param player the player to check
     * @return true if the player is an anti-viewer
     */
    boolean isAntiViewer(Player player);

    /**
     * Gets all anti-viewer UUIDs.
     *
     * @return collection of anti-viewer UUIDs
     */
    Collection<UUID> getAntiViewers();

    /**
     * Clears all anti-viewers.
     */
    void clearAntiViewers();

    /**
     * Sets the owner of this entity group.
     * Entities will only be visible to players who can see the owner.
     *
     * @param owner the owner player, or null to remove owner restrictions
     */
    void setOwner(@Nullable Player owner);

    /**
     * Gets the current owner of this entity group.
     *
     * @return the owner player, or null if no owner is set
     */
    @Nullable
    Player getOwner();

    /**
     * Gets the owner's UUID.
     *
     * @return the owner's UUID, or null if no owner is set
     */
    @Nullable
    UUID getOwnerUUID();

    /**
     * Sets the tracking range in blocks.
     *
     * @param range the range in blocks
     */
    void setTrackingRange(double range);

    /**
     * Gets the current tracking range.
     *
     * @return the tracking range in blocks
     */
    double getTrackingRange();

    /**
     * Sets the update interval in ticks.
     *
     * @param interval the interval in ticks
     */
    void setUpdateInterval(long interval);

    /**
     * Gets the current update interval.
     *
     * @return the update interval in ticks
     */
    long getUpdateInterval();

    /**
     * Sets the start delay in ticks.
     *
     * @param delay the delay in ticks
     */
    void setStartDelay(long delay);

    /**
     * Gets the current start delay.
     *
     * @return the start delay in ticks
     */
    long getStartDelay();

    /**
     * Sets a custom visibility predicate for determining if a player should see entities.
     * This is evaluated in addition to standard range and anti-viewer checks.
     *
     * @param predicate the visibility predicate, or null to remove custom rules
     */
    void setVisibilityPredicate(@Nullable Predicate<Player> predicate);

    /**
     * Sets a custom visibility predicate that considers both the potential viewer and owner.
     *
     * @param predicate the visibility predicate, or null to remove custom rules
     */
    void setOwnerVisibilityPredicate(@Nullable BiPredicate<Player, Player> predicate);

    /**
     * Forces all entities to respawn at a new location.
     *
     * @param location the new location
     */
    void respawnAt(Location location);

    /**
     * Gets the current tracking location (based on entities' positions).
     *
     * @return the tracking location, or null if no entities are present
     */
    @Nullable
    Location getTrackingLocation();

    /**
     * Schedules the tracker to be destroyed after a specified delay.
     *
     * @param ticks the delay in ticks
     */
    void destroyAfter(int ticks);

    /**
     * Forces an immediate update of all viewers.
     */
    void updateViewers();

    /**
     * Builder class for creating and configuring EntityTracker instances.
     */
    interface Builder {

        /**
         * Sets the tracking range in blocks.
         *
         * @param range the range in blocks
         * @return this builder
         */
        Builder trackingRange(double range);

        /**
         * Sets the update interval in ticks.
         *
         * @param interval the interval in ticks
         * @return this builder
         */
        Builder updateInterval(long interval);

        /**
         * Sets the start delay in ticks.
         *
         * @param delay the delay in ticks
         * @return this builder
         */
        Builder startDelay(long delay);

        /**
         * Sets the owner of the entity group.
         *
         * @param owner the owner player
         * @return this builder
         */
        Builder owner(@Nullable Player owner);

        /**
         * Sets a custom visibility predicate.
         *
         * @param predicate the visibility predicate
         * @return this builder
         */
        Builder visibilityPredicate(@Nullable Predicate<Player> predicate);

        /**
         * Sets a custom owner visibility predicate.
         *
         * @param predicate the owner visibility predicate
         * @return this builder
         */
        Builder ownerVisibilityPredicate(@Nullable BiPredicate<Player, Player> predicate);


        /**
         * Builds the EntityTracker with the configured settings.
         *
         * @return a new EntityTracker instance
         */
        EntityTracker build();
    }
}
