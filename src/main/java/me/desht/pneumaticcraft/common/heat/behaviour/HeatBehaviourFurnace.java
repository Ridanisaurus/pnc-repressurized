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
import me.desht.pneumaticcraft.mixin.accessors.AbstractFurnaceBlockEntityAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

import static me.desht.pneumaticcraft.api.PneumaticRegistry.RL;

public class HeatBehaviourFurnace extends HeatBehaviour {
    static final ResourceLocation ID = RL("furnace");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public boolean isApplicable() {
        return getBlockState().getBlock() instanceof AbstractFurnaceBlock;
    }

    @Override
    public void tick() {
        if (!(getCachedTileEntity() instanceof AbstractFurnaceBlockEntity furnace) || furnace.isRemoved()) {
            // shouldn't happen, but let's be defensive
            // https://github.com/TeamPneumatic/pnc-repressurized/issues/969
            return;
        }
        if (getHeatExchanger().getTemperature() > 373) {
            AbstractFurnaceBlockEntityAccess furnaceAccess = (AbstractFurnaceBlockEntityAccess) furnace;
            if (furnaceAccess.getLitTime() < 190 && !furnace.getItem(0).isEmpty()) {
                if (furnaceAccess.getLitTime() == 0) {
                    getWorld().setBlockAndUpdate(getPos(), getBlockState().setValue(AbstractFurnaceBlock.LIT, true));
                }
                furnaceAccess.setLitDuration(200);
                furnaceAccess.setLitTime(furnaceAccess.getLitTime() + 10);
                getHeatExchanger().addHeat(-1);
            }
            if (furnaceAccess.getCookingProgress() > 0) {
                // Easy performance saver, the Furnace won't be ticked unnecessarily when there's nothing to
                // cook (or when just started cooking).
                int progress = Math.max(0, ((int) getHeatExchanger().getTemperature() - 343) / 30);
                progress = Math.min(5, progress);
                for (int i = 0; i < progress; i++) {
                    AbstractFurnaceBlockEntity.serverTick(getWorld(), furnace.getBlockPos(), furnace.getBlockState(), furnace);
                }
            }
        }
    }

}
