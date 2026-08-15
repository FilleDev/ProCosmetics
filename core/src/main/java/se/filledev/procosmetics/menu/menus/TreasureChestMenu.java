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
package se.filledev.procosmetics.menu.menus;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import se.filledev.procosmetics.api.ProCosmetics;
import se.filledev.procosmetics.api.config.Config;
import se.filledev.procosmetics.api.menu.Menu;
import se.filledev.procosmetics.api.treasure.TreasureChest;
import se.filledev.procosmetics.api.treasure.TreasureChestPlatform;
import se.filledev.procosmetics.api.user.User;
import se.filledev.procosmetics.api.util.item.ItemBuilder;
import se.filledev.procosmetics.menu.MenuImpl;
import se.filledev.procosmetics.menu.menus.purchase.TreasurePurchaseMenu;
import se.filledev.procosmetics.util.item.ItemBuilderImpl;

import java.util.ArrayList;
import java.util.List;

public class TreasureChestMenu extends MenuImpl {

    private final Config config;
    private final List<TreasureChest> treasureChests = new ArrayList<>();
    private final int pageCount;
    private int page = 1;

    public TreasureChestMenu(ProCosmetics plugin, User user) {
        super(plugin, user, user.translate("menu.treasure_chests.title"),
                plugin.getTreasureChestManager().getTreasureChestsConfig().getInt("menu.rows")
        );
        this.config = plugin.getTreasureChestManager().getTreasureChestsConfig();

        for (TreasureChest treasureChest : plugin.getTreasureChestManager().getTreasureChests()) {
            if (treasureChest.isEnabled()) {
                treasureChests.add(treasureChest);
            }
        }

        this.pageCount = treasureChests.stream()
                .mapToInt(TreasureChest::getPage)
                .max()
                .orElse(1);
    }

    @Override
    protected void addItems() {
        for (TreasureChest treasureChest : treasureChests) {
            if (treasureChest.getPage() != page) {
                continue;
            }
            addTreasureChest(treasureChest);
        }
        addPageItem("next_page", page + 1, page < pageCount);
        addPageItem("previous_page", page - 1, page > 1);
    }

    private void addTreasureChest(TreasureChest treasureChest) {
        ItemBuilder itemBuilder = treasureChest.getItemBuilder();
        String name = treasureChest.getName(user);
        TagResolver tagResolver = treasureChest.getResolvers(user);
        String path;

        if (!treasureChest.isPurchasable()) {
            path = "purchase_disabled";
        } else if (treasureChest.hasPurchasePermission(player)) {
            path = "purchasable";
        } else {
            path = "purchase_no_permission";
        }

        itemBuilder.setDisplayName(user.translate(
                "menu.treasure_chests." + treasureChest.getKey() + "." + path + ".name",
                Placeholder.unparsed("name", name),
                tagResolver
        ));
        itemBuilder.setLore(user.translateList(
                "menu.treasure_chests." + treasureChest.getKey() + "." + path + ".desc",
                Placeholder.unparsed("name", name),
                tagResolver
        ));

        setItem(itemBuilder.getSlot(), itemBuilder.getItemStack(), event -> {
            if (event.isShiftClick() && config.getBoolean("loot_categories.enabled")) {
                Menu menu = new LootCategoriesMenu(plugin, user, treasureChest);
                menu.setPreviousMenu(this);
                menu.open();
                playClickSound();
                return;
            }

            if (user.getTreasureChests(treasureChest) < 1 || event.isRightClick()) {
                if (treasureChest.isPurchasable() && treasureChest.hasPurchasePermission(player)) {
                    new TreasurePurchaseMenu(plugin, user, treasureChest).open();
                    playClickSound();
                } else {
                    playDenySound();
                }
            } else {
                player.closeInventory();
                TreasureChestPlatform platform = user.getCurrentPlatform();

                if (platform == null) {
                    player.closeInventory();
                    return;
                }

                if (platform.isInUse()) {
                    user.sendMessage(user.translate("treasure_chest.already_in_use"));
                    playDenySound();
                    return;
                }
                platform.setUser(user);

                plugin.getDatabase().removeTreasureChestsAsync(user, treasureChest, 1).thenAcceptAsync(result -> {
                    if (result.leftBoolean()) {
                        treasureChest.getAnimationFactory().create(plugin, platform, treasureChest, user);
                    } else {
                        user.sendMessage(user.translate("generic.error.database"));
                        platform.setUser(null);
                        playDenySound();
                    }
                }, plugin.getSyncExecutor());
            }
        });
    }

    private void addPageItem(String key, int targetPage, boolean enabled) {
        ItemBuilderImpl itemBuilder = new ItemBuilderImpl(config, "menu.items." + key);

        if (!enabled) {
            removeItem(itemBuilder.getSlot());
            return;
        }
        itemBuilder.setDisplayName(user.translate("menu.treasure_chests." + key + ".name"));
        itemBuilder.setLore(user.translateList(
                "menu.treasure_chests." + key + ".desc",
                Placeholder.unparsed("page", String.valueOf(targetPage)),
                Placeholder.unparsed("pages", String.valueOf(pageCount))
        ));

        setItem(itemBuilder.getSlot(), itemBuilder.getItemStack(), event -> {
            page = targetPage;
            playClickSound();
            refresh();
        });
    }

    private void refresh() {
        clear();
        addItems();

        fillEmptySlots(inventory, getFillEmptySlotsItem());
    }

    @Override
    public @Nullable ItemStack getFillEmptySlotsItem() {
        if (!config.getBoolean("menu.items.fill_empty_slots.enabled")) {
            return null;
        }
        return new ItemBuilderImpl(config, "menu.items.fill_empty_slots").getItemStack();
    }
}
