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

package me.desht.pneumaticcraft.common.ai;

import me.desht.pneumaticcraft.common.progwidgets.ProgWidgetAreaItemBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class DroneAIEntityExport<W extends ProgWidgetAreaItemBase> extends DroneAIBlockInteraction<W> {
    public DroneAIEntityExport(IDroneBase drone, W progWidget) {
        super(drone, progWidget);
    }

    @Override
    public boolean canUse() {
        if (drone.getCarryingEntities().isEmpty()) return false;
        for (Entity e : drone.getCarryingEntities()) {
            if (!progWidget.isEntityValid(e)) return false;
        }
        return super.canUse();
    }

    @Override
    protected boolean isValidPosition(BlockPos pos) {
        return true;
    }

    @Override
    protected boolean moveIntoBlock() {
        return true;
    }

    @Override
    protected boolean doBlockInteraction(BlockPos pos, double squareDistToBlock) {
        drone.setCarryingEntity(null);
        return false;
    }
}
