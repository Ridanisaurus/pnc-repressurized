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

package me.desht.pneumaticcraft.common.dispenser;

import me.desht.pneumaticcraft.common.item.ItemDrone;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class BehaviorDispenseDrone extends DefaultDispenseItemBehavior {
    @Override
    protected ItemStack execute(IBlockSource source, ItemStack stack){
        Direction facing = source.getBlockState().getValue(DispenserBlock.FACING);
        BlockPos placePos = source.getPos().relative(facing);
        ((ItemDrone)stack.getItem()).spawnDrone(null, source.getLevel(), null, null, placePos, stack);
        
        stack.shrink(1);
        return stack;
    }
}
