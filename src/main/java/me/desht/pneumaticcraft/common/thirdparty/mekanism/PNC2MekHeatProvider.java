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

package me.desht.pneumaticcraft.common.thirdparty.mekanism;

import me.desht.pneumaticcraft.api.PNCCapabilities;
import me.desht.pneumaticcraft.api.heat.IHeatExchangerLogic;
import me.desht.pneumaticcraft.common.config.ConfigHelper;
import mekanism.api.heat.IHeatHandler;
import mekanism.api.heat.ISidedHeatHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * This capability object can be attached to PneumaticCraft heat-handling block entities to make them look like
 * Mekanism heat handlers.
 */
public class PNC2MekHeatProvider implements ICapabilityProvider {
    private final List<LazyOptional<IHeatHandler>> handlers = new ArrayList<>();

    private final WeakReference<BlockEntity> teRef;

    public PNC2MekHeatProvider(BlockEntity te) {
        teRef = new WeakReference<>(te);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        BlockEntity blockEntity = teRef.get();

        if (cap != MekanismIntegration.CAPABILITY_HEAT_HANDLER
                || blockEntity == null
                || !blockEntity.getCapability(PNCCapabilities.HEAT_EXCHANGER_CAPABILITY, side).isPresent())
        {
            return LazyOptional.empty();
        }

        if (handlers.isEmpty()) {
            // lazy init of the handlers list; this cap could be attached to any BE
            for (int i = 0; i < 7; i++) {  // 6 faces plus null face
                handlers.add(LazyOptional.empty());
            }
        }

        int idx = side == null ? 6 : side.get3DDataValue();
        if (!handlers.get(idx).isPresent()) {
            LazyOptional<IHeatExchangerLogic> heatExchanger = blockEntity.getCapability(PNCCapabilities.HEAT_EXCHANGER_CAPABILITY, side);
            if (heatExchanger.isPresent()) {
                heatExchanger.addListener(l -> handlers.set(idx, LazyOptional.empty()));
                PNC2MekHeatAdapter adapter = new PNC2MekHeatAdapter(side, heatExchanger);
                handlers.set(idx, LazyOptional.of(() -> adapter));
            }
        }

        //noinspection unchecked
        return (LazyOptional<T>) handlers.get(idx);
    }

    public record PNC2MekHeatAdapter(Direction side, LazyOptional<IHeatExchangerLogic> heatExchanger)
            implements ISidedHeatHandler
    {
        @Nullable
        @Override
        public Direction getHeatSideFor() {
            return side;
        }

        @Override
        public int getHeatCapacitorCount(@Nullable Direction direction) {
            return heatExchanger.isPresent() ? 1 : 0;
        }

        @Override
        public double getTemperature(int i, @Nullable Direction direction) {
            return heatExchanger.map(IHeatExchangerLogic::getTemperature).orElse(0d);
        }

        @Override
        public double getInverseConduction(int i, @Nullable Direction direction) {
            return heatExchanger.map(h -> h.getThermalResistance() * ConfigHelper.common().integration.mekThermalResistanceFactor.get()).orElse(1d);
        }

        @Override
        public double getHeatCapacity(int i, @Nullable Direction direction) {
            return heatExchanger.map(IHeatExchangerLogic::getThermalCapacity).orElse(0d);
        }

        @Override
        public void handleHeat(int i, double amount, @Nullable Direction direction) {
            heatExchanger.ifPresent(h -> h.addHeat(amount * ConfigHelper.common().integration.mekThermalEfficiencyFactor.get()));
        }
    }
}
