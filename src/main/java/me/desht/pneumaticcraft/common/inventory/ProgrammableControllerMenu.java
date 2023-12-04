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

import me.desht.pneumaticcraft.common.block.entity.ProgrammableControllerBlockEntity;
import me.desht.pneumaticcraft.common.core.ModMenuTypes;
import me.desht.pneumaticcraft.common.network.SyncedField;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.SlotItemHandler;

public class ProgrammableControllerMenu extends AbstractPneumaticCraftMenu<ProgrammableControllerBlockEntity> {

    public ProgrammableControllerMenu(int i, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(i, playerInventory, getTilePos(buffer));
    }

    public ProgrammableControllerMenu(int i, Inventory playerInventory, BlockPos pos) {
        super(ModMenuTypes.PROGRAMMABLE_CONTROLLER.get(), i, playerInventory, pos);

        addSlot(new SlotItemHandler(te.getPrimaryInventory(), 0, 89, 36));

        addUpgradeSlots(39, 29);

        addPlayerSlots(playerInventory, 84);

        te.getCapability(ForgeCapabilities.ENERGY).ifPresent(handler -> {
            try {
                addSyncedField(new SyncedField.SyncedInt(handler, EnergyStorage.class.getDeclaredField("energy")));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        });
    }
}
