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

package me.desht.pneumaticcraft.api.semiblock;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Event;

public class SemiblockEvent extends Event {
    private final World world;
    private final BlockPos pos;
    private final ISemiBlock semiblock;

    private SemiblockEvent(World world, BlockPos pos, ISemiBlock semiblock) {
        this.world = world;
        this.pos = pos;
        this.semiblock = semiblock;
    }

    public World getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }

    public ISemiBlock getSemiblock() {
        return semiblock;
    }

    public Direction getSide() {
        return semiblock instanceof IDirectionalSemiblock ? ((IDirectionalSemiblock) semiblock).getSide() : null;
    }

    /**
     * Fired when a semiblock is added to the world.
     */
    public static class PlaceEvent extends SemiblockEvent {
        public PlaceEvent(World world, BlockPos pos, ISemiBlock semiBlock) {
            super(world, pos, semiBlock);
        }
    }

    /**
     * Fired when a semiblock is removed from the world.
     */
    public static class BreakEvent extends SemiblockEvent {
        public BreakEvent(World world, BlockPos pos, ISemiBlock semiBlock) {
            super(world, pos, semiBlock);
        }
    }
}
