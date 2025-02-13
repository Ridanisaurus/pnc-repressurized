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
import me.desht.pneumaticcraft.common.tileentity.TileEntityTagWorkbench;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerTagWorkbench extends ContainerPneumaticBase<TileEntityTagWorkbench> {
    public ContainerTagWorkbench(int windowId, PlayerInventory inv, BlockPos pos) {
        super(ModContainers.TAG_MATCHER.get(), windowId, inv, pos);

        addSlot(new SlotItemHandler(te.getPrimaryInventory(), 0, 8, 18));
        addSlot(new SlotItemHandler(te.getPrimaryInventory(), 1, 123, 18));
        addSlot(new SlotOutput(te.getPrimaryInventory(), 2, 206, 18));

        addPlayerSlots(inv, 34, 174);
    }

    public ContainerTagWorkbench(int windowId, PlayerInventory invPlayer, PacketBuffer extra) {
        this(windowId, invPlayer, getTilePos(extra));
    }
}
