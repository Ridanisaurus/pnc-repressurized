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

package me.desht.pneumaticcraft.common.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;

/**
 * To keep crafting widgets happy
 */
public class DummyContainer extends Container {
    public DummyContainer() {
        super(null, -1);
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return false;
    }

    @Override
    public void slotsChanged(IInventory inventoryIn) {
        // do nothing; default behaviour is to call detectAndSendChanges() which is unnecessary for drone
        // crafting purposes, and just wastes CPU cycles
    }
}
