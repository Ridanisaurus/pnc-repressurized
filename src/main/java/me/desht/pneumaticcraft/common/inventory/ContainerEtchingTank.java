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

import me.desht.pneumaticcraft.common.core.ModContainers;
import me.desht.pneumaticcraft.common.core.ModItems;
import me.desht.pneumaticcraft.common.tileentity.TileEntityEtchingTank;
import me.desht.pneumaticcraft.common.tileentity.TileEntityUVLightBox;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ContainerEtchingTank extends ContainerPneumaticBase<TileEntityEtchingTank> {
    public ContainerEtchingTank(int windowId, PlayerInventory playerInv, BlockPos pos) {
        super(ModContainers.ETCHING_TANK.get(), windowId, playerInv, pos);

        for (int i = 0; i < TileEntityEtchingTank.ETCHING_SLOTS; i++) {
            int x = 8 + 18 * (i % 5);
            int y = 18 + 18 * (i / 5);
            addSlot(new SlotPCB(te.getPrimaryInventory(), i, x, y));
        }

        addSlot(new SlotOutput(te.getOutputHandler(), 0, 104, 18));
        addSlot(new SlotOutput(te.getFailedHandler(), 0, 104, 90));

        addPlayerSlots(playerInv, 125);

    }

    public ContainerEtchingTank(int windowId, PlayerInventory playerInv, PacketBuffer buffer) {
        this(windowId, playerInv, getTilePos(buffer));
    }

    private static class SlotPCB extends SlotItemHandler {
        SlotPCB(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return stack.getItem() == ModItems.EMPTY_PCB.get() && TileEntityUVLightBox.getExposureProgress(stack) > 0;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}
