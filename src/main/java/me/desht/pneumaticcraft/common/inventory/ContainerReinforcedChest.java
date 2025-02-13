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
import me.desht.pneumaticcraft.common.tileentity.TileEntityReinforcedChest;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerReinforcedChest extends ContainerPneumaticBase<TileEntityReinforcedChest> {
    public ContainerReinforcedChest(int windowId, PlayerInventory invPlayer, BlockPos pos) {
        super(ModContainers.REINFORCED_CHEST.get(), windowId, invPlayer, pos);

        for (int i = 0; i < TileEntityReinforcedChest.CHEST_SIZE; i++) {
            addSlot(new SlotItemHandler(te.getPrimaryInventory(), i, 8 + (i % 9) * 18, 18 + (i / 9) * 18));
        }
        addPlayerSlots(invPlayer, 104);
    }

    public ContainerReinforcedChest(int windowId, PlayerInventory invPlayer, PacketBuffer buffer) {
        this(windowId, invPlayer, getTilePos(buffer));
    }
}
