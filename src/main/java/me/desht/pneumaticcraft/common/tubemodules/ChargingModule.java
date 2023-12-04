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

package me.desht.pneumaticcraft.common.tubemodules;

import me.desht.pneumaticcraft.api.PNCCapabilities;
import me.desht.pneumaticcraft.common.block.entity.AdvancedPressureTubeBlockEntity;
import me.desht.pneumaticcraft.common.block.entity.PressureTubeBlockEntity;
import me.desht.pneumaticcraft.common.core.ModItems;
import me.desht.pneumaticcraft.lib.PneumaticValues;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public class ChargingModule extends AbstractTubeModule {
    private BlockEntity neighbourTE = null;

    public ChargingModule(Direction dir, PressureTubeBlockEntity pressureTube) {
        super(dir, pressureTube);
    }

    @Override
    public Item getItem() {
        return ModItems.CHARGING_MODULE.get();
    }

    @Override
    public void tickServer() {
        super.tickServer();

        if (pressureTube.nonNullLevel().isClientSide || (pressureTube.nonNullLevel().getGameTime() & 0x7) != 0) return;

        getConnectedInventory().ifPresent(itemHandler -> {
            // times 8 because we only run every 8 ticks
            int advMul = pressureTube instanceof AdvancedPressureTubeBlockEntity ? 4 : 1;
            int transferLimit = 8 * PneumaticValues.CHARGING_STATION_CHARGE_RATE * (upgraded ? 10 : 1) * advMul;
            pressureTube.getCapability(PNCCapabilities.AIR_HANDLER_MACHINE_CAPABILITY).ifPresent(airHandler -> {
                for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
                    ItemStack chargedItem = itemHandler.getStackInSlot(slot);
                    if (chargedItem.getCount() != 1) continue;
                    chargedItem.getCapability(PNCCapabilities.AIR_HANDLER_ITEM_CAPABILITY).ifPresent(airHandlerItem -> {
                        float itemPressure = airHandlerItem.getPressure();
                        float modulePressure = airHandler.getPressure();
                        float delta = Math.abs(modulePressure - itemPressure) / 2.0F;
                        int airInItem = airHandlerItem.getAir();
                        if (itemPressure > modulePressure + 0.01F && itemPressure > 0F) {
                            // move air from item to charger (tube)
                            int airToMove = Math.min(Math.min(transferLimit, airInItem), (int) (delta * airHandler.getVolume()));
                            airHandlerItem.addAir(-airToMove);
                            airHandler.addAir(airToMove);
                        } else if (itemPressure < modulePressure - 0.01F && itemPressure < airHandlerItem.maxPressure()) {
                            // move air from charger (tube) to item
                            float itemVolume = airHandlerItem.getVolume();
                            int maxAirInItem = (int) (airHandlerItem.maxPressure() * itemVolume);
                            int airToMove = Math.min(Math.min(transferLimit, airHandler.getAir()), (int) (delta * itemVolume));
                            airToMove = Math.min(maxAirInItem - airInItem, airToMove);
                            airHandlerItem.addAir(airToMove);
                            airHandler.addAir(-airToMove);
                        }
                    });
                }
            });
        });
    }

    private LazyOptional<IItemHandler> getConnectedInventory() {
        if (neighbourTE == null || neighbourTE.isRemoved()) {
            neighbourTE = pressureTube.nonNullLevel().getBlockEntity(pressureTube.getBlockPos().relative(dir));
        }
        return neighbourTE == null ? LazyOptional.empty() : neighbourTE.getCapability(ForgeCapabilities.ITEM_HANDLER, dir.getOpposite());
    }
}
