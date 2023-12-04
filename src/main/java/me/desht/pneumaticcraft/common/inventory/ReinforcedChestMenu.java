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

import me.desht.pneumaticcraft.common.block.entity.ReinforcedChestBlockEntity;
import me.desht.pneumaticcraft.common.core.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.SlotItemHandler;

public class ReinforcedChestMenu extends AbstractPneumaticCraftMenu<ReinforcedChestBlockEntity> {
    public ReinforcedChestMenu(int windowId, Inventory invPlayer, BlockPos pos) {
        super(ModMenuTypes.REINFORCED_CHEST.get(), windowId, invPlayer, pos);

        for (int i = 0; i < ReinforcedChestBlockEntity.CHEST_SIZE; i++) {
            addSlot(new SlotItemHandler(te.getPrimaryInventory(), i, 8 + (i % 9) * 18, 18 + (i / 9) * 18));
        }
        addPlayerSlots(invPlayer, 104);
    }

    public ReinforcedChestMenu(int windowId, Inventory invPlayer, FriendlyByteBuf buffer) {
        this(windowId, invPlayer, getTilePos(buffer));
    }
}
