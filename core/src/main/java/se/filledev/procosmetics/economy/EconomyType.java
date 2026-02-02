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
package se.filledev.procosmetics.economy;

import se.filledev.procosmetics.api.ProCosmetics;
import se.filledev.procosmetics.api.economy.EconomyProvider;
import se.filledev.procosmetics.economy.type.BuiltInEconomy;
import se.filledev.procosmetics.economy.type.PlayerPointsEconomy;
import se.filledev.procosmetics.economy.type.VaultEconomy;

import java.util.function.Function;

public enum EconomyType {

    BUILT_IN("Built-In", BuiltInEconomy::new),
    VAULT("Vault", plugin -> new VaultEconomy()),
    PLAYER_POINTS("Player Points", plugin -> new PlayerPointsEconomy()),
    CUSTOM("Custom", null);

    private final String name;
    private final Function<ProCosmetics, EconomyProvider> factory;

    EconomyType(String name, Function<ProCosmetics, EconomyProvider> factory) {
        this.name = name;
        this.factory = factory;
    }

    public String getName() {
        return name;
    }

    public EconomyProvider create(ProCosmetics plugin) {
        return factory != null ? factory.apply(plugin) : null;
    }
}
