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

import me.desht.pneumaticcraft.api.client.pneumatic_helmet.IHackableEntity;
import me.desht.pneumaticcraft.client.util.ClientUtils;
import me.desht.pneumaticcraft.common.core.ModSounds;
import me.desht.pneumaticcraft.common.event.HackTickHandler;
import me.desht.pneumaticcraft.common.hacking.HackManager;
import me.desht.pneumaticcraft.common.pneumatic_armor.ArmorUpgradeRegistry;
import me.desht.pneumaticcraft.common.pneumatic_armor.CommonArmorHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Received on: CLIENT
 * Sent by server when an entity hack completes
 */
public class PacketHackingEntityFinish {
    private final int entityId;

    public PacketHackingEntityFinish(Entity entity) {
        entityId = entity.getId();
    }

    public PacketHackingEntityFinish(PacketBuffer buffer) {
        this.entityId = buffer.readInt();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(entityId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ClientUtils.getClientPlayer();
            Entity entity = player.level.getEntity(entityId);
            if (entity != null) {
                IHackableEntity hackableEntity = HackManager.getHackableForEntity(entity, player);
                if (hackableEntity != null) {
                    hackableEntity.onHackFinished(entity, player);
                    HackTickHandler.instance().trackEntity(entity, hackableEntity);
                    CommonArmorHandler.getHandlerForPlayer(player).getExtensionData(ArmorUpgradeRegistry.getInstance().hackHandler).setHackedEntity(null);
                    player.playSound(ModSounds.HELMET_HACK_FINISH.get(), 1.0F, 1.0F);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
