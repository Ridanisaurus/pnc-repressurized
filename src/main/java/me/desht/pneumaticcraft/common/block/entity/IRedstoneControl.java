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

package me.desht.pneumaticcraft.common.block.entity;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

import static me.desht.pneumaticcraft.common.util.PneumaticCraftUtils.xlate;

@FunctionalInterface
public interface IRedstoneControl<T extends BlockEntity & IRedstoneControl<T>> {
    /**
     * Get the redstone controller object for this BE
     *
     * @return the redstone controller
     */
    RedstoneController<T> getRedstoneController();

    default MutableComponent getRedstoneTabTitle() {
        return ((IRedstoneControl<?>) this).getRedstoneController().isEmitter() ?
                xlate("pneumaticcraft.gui.tab.redstoneBehaviour.emitRedstoneWhen") :
                xlate("pneumaticcraft.gui.tab.redstoneBehaviour.enableOn");
    }

    /**
     * Get the current redstone level for this BE.
     *
     * @return the current redstone level for the BE
     */
    default int getCurrentRedstonePower() {
        return getRedstoneController().getCurrentRedstonePower();
    }

    default int getRedstoneMode() {
        return getRedstoneController().getCurrentMode();
    }

    default void onRedstoneModeChanged(int newModeIdx) { }
}
