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

import me.desht.pneumaticcraft.client.pneumatic_armor.ArmorUpgradeClientRegistry;
import me.desht.pneumaticcraft.client.render.pneumatic_armor.RenderEntityTarget;
import me.desht.pneumaticcraft.client.render.pneumatic_armor.upgrade_handler.EntityTrackerClientHandler;
import me.desht.pneumaticcraft.client.util.ClientUtils;
import me.desht.pneumaticcraft.common.pneumatic_armor.ArmorUpgradeRegistry;
import me.desht.pneumaticcraft.common.pneumatic_armor.CommonArmorHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Received on: BOTH
 * Sent by client when player initiates an entity hack, and by server to confirm initiation
 */
public class PacketHackingEntityStart {
    private final int entityId;

    public PacketHackingEntityStart(Entity entity) {
        entityId = entity.getId();
    }

    public PacketHackingEntityStart(PacketBuffer buffer) {
        entityId = buffer.readInt();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(entityId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            ArmorUpgradeRegistry r = ArmorUpgradeRegistry.getInstance();
            if (player == null) {
                // client
                PlayerEntity cPlayer = ClientUtils.getClientPlayer();
                Entity entity = cPlayer.level.getEntity(entityId);
                if (entity != null) {
                    CommonArmorHandler.getHandlerForPlayer(cPlayer)
                            .getExtensionData(r.hackHandler)
                            .setHackedEntity(entity);
                    ArmorUpgradeClientRegistry.getInstance()
                            .getClientHandler(r.entityTrackerHandler, EntityTrackerClientHandler.class)
                            .getTargetsStream()
                            .filter(target -> target.entity == entity)
                            .findFirst()
                            .ifPresent(RenderEntityTarget::onHackConfirmServer);
                }
            } else {
                // server
                CommonArmorHandler handler = CommonArmorHandler.getHandlerForPlayer(player);
                if (handler.upgradeUsable(r.entityTrackerHandler, true)) {
                    Entity entity = player.level.getEntity(entityId);
                    if (entity != null) {
                        handler.getExtensionData(r.hackHandler).setHackedEntity(entity);
                        NetworkHandler.sendToAllTracking(this, entity);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
