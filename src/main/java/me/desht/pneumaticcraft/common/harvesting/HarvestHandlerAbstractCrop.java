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

package me.desht.pneumaticcraft.common.harvesting;

import me.desht.pneumaticcraft.api.drone.IDrone;
import me.desht.pneumaticcraft.api.harvesting.HarvestHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Predicate;

public abstract class HarvestHandlerAbstractCrop extends HarvestHandler {

    private final Predicate<BlockState> blockChecker;
    
    public HarvestHandlerAbstractCrop(Predicate<BlockState> blockChecker){
        this.blockChecker = blockChecker;
    }
    
    @Override
    public boolean canHarvest(World world, IBlockReader chunkCache, BlockPos pos, BlockState state, IDrone drone){
        return blockChecker.test(state) && isMaxAge(state);
    }
    
    @Override
    public boolean harvestAndReplant(World world, IBlockReader chunkCache, BlockPos pos, BlockState state, IDrone drone){
        harvest(world, chunkCache, pos, state, drone);
        List<ItemEntity> seedItems = world.getEntitiesOfClass(ItemEntity.class, new AxisAlignedBB(pos), entityItem -> isSeed(world, pos, state, entityItem.getItem()));
        if(!seedItems.isEmpty()){
            seedItems.get(0).getItem().shrink(1);//Use a seed
            world.setBlockAndUpdate(pos, withMinAge(state)); //And plant it.
            return true;
        }else{
            return false;
        }
    }
    
    protected abstract boolean isSeed(World world, BlockPos pos, BlockState state, ItemStack stack);
    
    protected abstract boolean isMaxAge(BlockState state);
    
    protected abstract BlockState withMinAge(BlockState state);
}
