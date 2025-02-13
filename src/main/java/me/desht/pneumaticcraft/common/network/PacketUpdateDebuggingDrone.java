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

package me.desht.pneumaticcraft.common.network;

import me.desht.pneumaticcraft.api.lib.NBTKeys;
import me.desht.pneumaticcraft.common.ai.IDroneBase;
import me.desht.pneumaticcraft.common.pneumatic_armor.ArmorUpgradeRegistry;
import me.desht.pneumaticcraft.common.pneumatic_armor.CommonArmorHandler;
import me.desht.pneumaticcraft.common.util.NBTUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

/**
 * Received on: SERVER
 * Sent by client when the drone debug key is pressed, for a valid entity or programmable controller target
 */
public class PacketUpdateDebuggingDrone extends PacketDroneDebugBase {
    public PacketUpdateDebuggingDrone(int entityId) {
        super(entityId, null);
    }

    public PacketUpdateDebuggingDrone(BlockPos controllerPos) {
        super(-1, controllerPos);
    }

    public PacketUpdateDebuggingDrone(PacketBuffer buf) {
        super(buf);
    }

    @Override
    void handle(PlayerEntity player, IDroneBase droneBase) {
        if (player instanceof ServerPlayerEntity) {
            CommonArmorHandler handler = CommonArmorHandler.getHandlerForPlayer(player);
            if (handler.upgradeUsable(ArmorUpgradeRegistry.getInstance().droneDebugHandler, false)) {
                ItemStack stack = player.getItemBySlot(EquipmentSlotType.HEAD);
                if (droneBase == null) {
                    NBTUtils.removeTag(stack, NBTKeys.PNEUMATIC_HELMET_DEBUGGING_DRONE);
                    NBTUtils.removeTag(stack, NBTKeys.PNEUMATIC_HELMET_DEBUGGING_PC);
                } else {
                    droneBase.storeTrackerData(stack);
                    droneBase.getDebugger().trackAsDebugged((ServerPlayerEntity) player);
                }
            }
        }
    }
}
