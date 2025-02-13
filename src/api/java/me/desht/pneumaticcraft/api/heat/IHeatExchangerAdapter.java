/*
 * This file is part of pnc-repressurized API.
 *
 *     pnc-repressurized API is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     pnc-repressurized is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with pnc-repressurized API.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.desht.pneumaticcraft.api.heat;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import java.util.function.BiPredicate;

/**
 * A special extension of {@link IHeatExchangerLogic} intended for implementing adapter objects for other mods' heat
 * systems.  An implementation of this can be attached to other mods' tile entities to make them appear like PNC heat
 * exchanging blocks, while internally adapting the PNC heat API to the block's actual heat API.
 */
public interface IHeatExchangerAdapter extends IHeatExchangerLogic {
    @Override
    default void tick() {
    }

    @Override
    default void initializeAsHull(World world, BlockPos pos, BiPredicate<IWorld, BlockPos> blockFilter, Direction... validSides) {
    }

    @Override
    default void initializeAmbientTemperature(World world, BlockPos pos) {
    }

    @Override
    default void setTemperature(double temperature) {
    }

    @Override
    default int getTemperatureAsInt() {
        return (int) getTemperature();
    }

    @Override
    default void setThermalResistance(double thermalResistance) {
    }

    @Override
    default void setThermalCapacity(double capacity) {
    }

    /**
     * Convenience adapter implementation which can be extended.  Handles sidedness and ambient temperature
     * automatically, as well as storing the other mod's heat capability object for adapting purposes.
     *
     * @param <CAP> the interface object for the other mod's heat capability object
     */
    abstract class Simple<CAP> implements IHeatExchangerAdapter {
        protected final Direction side;
        protected final LazyOptional<CAP> foreignHeatCap;
        protected final double ambientTemperature;

        public Simple(Direction side, LazyOptional<CAP> foreignHeatCap, double ambientTemperature) {
            this.side = side;
            this.foreignHeatCap = foreignHeatCap;
            this.ambientTemperature = ambientTemperature;
        }

        @Override
        public double getAmbientTemperature() {
            return ambientTemperature;
        }

        @Override
        public boolean isSideConnected(Direction side) {
            return this.side == side;
        }
    }
}
