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

package me.desht.pneumaticcraft.common.heat.behaviour;

import me.desht.pneumaticcraft.api.heat.HeatBehaviour;
import me.desht.pneumaticcraft.api.heat.IHeatExchangerLogic;
import me.desht.pneumaticcraft.lib.Log;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public enum HeatBehaviourManager {
    INSTANCE;

    private final Map<ResourceLocation, Supplier<? extends HeatBehaviour<?>>> behaviourRegistry = new HashMap<>();

    public static HeatBehaviourManager getInstance() {
        return INSTANCE;
    }

    public void init() {
        behaviourRegistry.clear();

        registerBehaviour(HeatBehaviourFurnace.ID, HeatBehaviourFurnace::new);
        registerBehaviour(HeatBehaviourHeatFrame.ID, HeatBehaviourHeatFrame::new);

        // this handles all custom non-tile-entity blocks and fluids, both vanilla and modded
        registerBehaviour(HeatBehaviourCustomTransition.ID, HeatBehaviourCustomTransition::new);
    }

    public void registerBehaviour(ResourceLocation id, Supplier<? extends HeatBehaviour<?>> behaviour) {
        Validate.notNull(behaviour);

        Supplier<? extends HeatBehaviour<?>> existing = behaviourRegistry.put(id, behaviour);
        if (existing != null) Log.warning("Overriding heat behaviour " + id);
    }

    public <T extends HeatBehaviour<?>> T createBehaviour(ResourceLocation id) {
        Supplier<? extends HeatBehaviour<?>> behaviour = behaviourRegistry.get(id);
        if (behaviour != null) {
            //noinspection unchecked
            return (T) behaviour.get();
        } else {
            Log.warning("No heat behaviour found for id: " + id);
            return null;
        }
    }

    public int addHeatBehaviours(World world, BlockPos pos, Direction direction, BiPredicate<IWorld, BlockPos> blockFilter, IHeatExchangerLogic logic, List<HeatBehaviour<?>> list) {
        if (!blockFilter.test(world, pos)) return 0;
        int s = list.size();
        for (Supplier<? extends HeatBehaviour<?>> bSup : behaviourRegistry.values()) {
            HeatBehaviour<?> behaviour = bSup.get().initialize(logic, world, pos, direction);
            if (behaviour.isApplicable()) {
                list.add(behaviour);
            }
        }
        return list.size() - s;
    }
}
