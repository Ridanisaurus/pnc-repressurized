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

import me.desht.pneumaticcraft.common.block.entity.FluidMixerBlockEntity;
import me.desht.pneumaticcraft.common.core.ModMenuTypes;
import me.desht.pneumaticcraft.common.inventory.slot.OutputOnlySlot;
import me.desht.pneumaticcraft.common.inventory.slot.UpgradeSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class FluidMixerMenu extends AbstractPneumaticCraftMenu<FluidMixerBlockEntity> {
    public FluidMixerMenu(int windowId, Inventory inv, BlockPos pos) {
        super(ModMenuTypes.FLUID_MIXER.get(), windowId, inv, pos);

        addSlot(new OutputOnlySlot(te.getPrimaryInventory(), 0, 73, 67));

        for (int i = 0; i < 4; i++) {
            addSlot(new UpgradeSlot(te, i, 98 + i * 18, 106));
        }
        addPlayerSlots(inv, 130);
    }

    public FluidMixerMenu(int windowId, Inventory invPlayer, FriendlyByteBuf extra) {
        this(windowId, invPlayer, getTilePos(extra));
    }
}
