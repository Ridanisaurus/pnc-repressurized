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

package me.desht.pneumaticcraft.common.thirdparty.immersiveengineering;

import blusunrize.immersiveengineering.api.Lib;
import me.desht.pneumaticcraft.api.PNCCapabilities;
import me.desht.pneumaticcraft.api.item.EnumUpgrade;
import me.desht.pneumaticcraft.common.core.ModSounds;
import me.desht.pneumaticcraft.common.entity.living.EntityDrone;
import me.desht.pneumaticcraft.common.network.NetworkHandler;
import me.desht.pneumaticcraft.common.network.PacketSpawnParticle;
import me.desht.pneumaticcraft.common.particle.AirParticleData;
import me.desht.pneumaticcraft.common.pneumatic_armor.CommonArmorHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ElectricAttackHandler {
    private static final Map<UUID, Long> sounds = new HashMap<>();

    @SubscribeEvent
    public static void onElectricalAttack(LivingHurtEvent event) {
        if (!event.getSource().getMsgId().equals(Lib.DMG_WireShock)) return;

        if (event.getEntityLiving() instanceof EntityDrone) {
            EntityDrone drone = (EntityDrone) event.getEntityLiving();
            float dmg = event.getAmount();
            int sec = drone.getUpgrades(EnumUpgrade.SECURITY);
            if (sec > 0) {
                drone.getCapability(PNCCapabilities.AIR_HANDLER_CAPABILITY).orElseThrow(RuntimeException::new).addAir((int)(-50 * dmg));
                event.setAmount(0f);
                double dy = Math.min(dmg / 4, 0.5);
                NetworkHandler.sendToAllTracking(new PacketSpawnParticle(AirParticleData.DENSE, drone.getX(), drone.getY(), drone.getZ(),
                            0, -dy, 0, (int) (dmg), 0, 0, 0), drone);
                playLeakSound(drone);
            }
        } else if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)event.getEntityLiving();
            CommonArmorHandler handler = CommonArmorHandler.getHandlerForPlayer(player);
            if (handler.getUpgradeCount(EquipmentSlotType.CHEST, EnumUpgrade.SECURITY) > 0
                    && handler.getArmorPressure(EquipmentSlotType.CHEST) > 0.1
                    && handler.isArmorReady(EquipmentSlotType.CHEST)) {
                handler.addAir(EquipmentSlotType.CHEST, (int)(-150 * event.getAmount()));
                float sx = player.getRandom().nextFloat() * 1.5F - 0.75F;
                float sz = player.getRandom().nextFloat() * 1.5F - 0.75F;
                double dy = Math.min(event.getAmount() / 4, 0.5);
                NetworkHandler.sendToAllTracking(new PacketSpawnParticle(AirParticleData.DENSE, player.getX() + sx, player.getY() + 1, player.getZ() + sz, sx / 4, -dy, sz / 4), player.level, player.blockPosition());
                event.setAmount(0f);
                playLeakSound(player);
            }
        }
    }

    private static void playLeakSound(Entity e) {
        if (e.level.getGameTime() - sounds.getOrDefault(e.getUUID(), 0L) > 16) {
            e.level.playSound(null, e.blockPosition(), ModSounds.LEAKING_GAS.get(), SoundCategory.PLAYERS, 0.5f, 0.7f);
            sounds.put(e.getUUID(), e.level.getGameTime());
        }
    }
}
