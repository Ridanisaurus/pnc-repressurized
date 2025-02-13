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

package me.desht.pneumaticcraft.api.tileentity;

import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Optionally implement this in your TileEntity to gain extra control over pneumatic behaviour.
 * These methods will be called by the {@link IAirHandlerMachine} implementation when it is ticked.  Note that all
 * of these methods have default "no-op" implementations, so override the ones you need to.
 */
public interface IAirListener {
    /**
     * Called when air is added to, or removed from a handler, dispersed into/from a certain direction.  Used by the
     * Flow Detector Module, for example.
     *
     * @param handler the air handler in question
     * @param dir the direction of air dispersal
     * @param airDispersed the amount of air dispersed to the neighbouring handler (negative when air is being added to this handler)
     */
    default void onAirDispersion(IAirHandlerMachine handler, @Nullable Direction dir, int airDispersed) {
    }

    /**
     * Method fired to get the maximum amount of air allowed to disperse to the given direction. Used in the Regulator
     * Tube Module, for example, to limit air flow.
     *
     * @param handler the air handler in question
     * @param dir the direction of dispersal
     * @return the max amount of air which may be dispersed this tick (return Integer.MAX_VALUE to have no limit)
     */
    default int getMaxDispersion(IAirHandlerMachine handler, @Nullable Direction dir) { return Integer.MAX_VALUE; }

    /**
     * With this method, you can add neighbouring air handlers that aren't physically adjacent, but should be considered
     * connected for air dispersion logic. Used in Pressure Chamber Valves, for example, to make them connect when they
     * are part of the same Pressure Chamber.
     *
     * @param airHandlers add extra connected air handlers to this list
     * @return the supplied list, for convenience
     */
    default List<IAirHandlerMachine> addConnectedPneumatics(List<IAirHandlerMachine> airHandlers) {
        return airHandlers;
    }
}
