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

package me.desht.pneumaticcraft.common.entity.living;

import me.desht.pneumaticcraft.common.core.ModEntities;
import me.desht.pneumaticcraft.common.progwidgets.*;
import me.desht.pneumaticcraft.common.util.DroneProgramBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class EntityGuardDrone extends EntityBasicDrone {
    public EntityGuardDrone(EntityType<? extends EntityDrone> type, World world) {
        super(type, world);
    }

    public EntityGuardDrone(World world, PlayerEntity player) {
        super(ModEntities.GUARD_DRONE.get(), world, player);
    }

    @Override
    public boolean addProgram(BlockPos clickPos, Direction facing, BlockPos pos, ItemStack droneStack, List<IProgWidget> widgets) {
        DroneProgramBuilder builder = new DroneProgramBuilder();
        builder.add(new ProgWidgetStart());
        // no item filter because we don't know what type of sword or ammo could be in the inventory
        builder.add(new ProgWidgetInventoryImport(), ProgWidgetArea.fromPosition(clickPos));
        builder.add(new ProgWidgetEntityAttack(),
                ProgWidgetArea.fromPositions(clickPos.offset(-16, -5, -16), clickPos.offset(16, 8, 16)),
                ProgWidgetText.withText("@mob")
        );
        maybeAddStandbyInstruction(builder, droneStack);
        builder.add(new ProgWidgetWait(), ProgWidgetText.withText("10"));
        widgets.addAll(builder.build());

        return true;
    }
}
