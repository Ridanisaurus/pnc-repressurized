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

package me.desht.pneumaticcraft.common.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotFluidContainer extends SlotItemHandler {
    private final int minFluid;

    SlotFluidContainer(IItemHandler handler, int index, int x, int y, int minFluid) {
        super(handler, index, x, y);

        this.minFluid = minFluid;
    }

    SlotFluidContainer(IItemHandler handler, int index, int x, int y) {
        super(handler, index, x, y);

        this.minFluid = 0;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        if (FluidUtil.getFluidHandler(stack).isPresent()) {
            return FluidUtil.getFluidContained(stack).map(fluidStack -> fluidStack.getAmount() >= minFluid).orElse(minFluid == 0);
        } else {
            return false;
        }
    }
}
