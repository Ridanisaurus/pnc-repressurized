/*
 * This file is part of pnc-repressurized.
 *
 *     pnc-repressurized is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     pnc-repressurized is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with pnc-repressurized.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.desht.pneumaticcraft.common.thirdparty;

import net.minecraft.util.DamageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * For checking if a damage source type is radiation in a mod-agnostic way.
 */
public enum RadiationSourceCheck {
    INSTANCE;

    private final List<Predicate<DamageSource>> radiationSources = new ArrayList<>();

    public void registerRadiationSource(Predicate<DamageSource> predicate) {
        radiationSources.add(predicate);
    }

    public boolean isRadiation(DamageSource source) {
        return radiationSources.stream().anyMatch(p -> p.test(source));
    }
}
