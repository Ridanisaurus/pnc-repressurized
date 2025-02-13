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

package me.desht.pneumaticcraft.common.tileentity;

import me.desht.pneumaticcraft.api.PneumaticRegistry;
import me.desht.pneumaticcraft.api.heat.IHeatExchangerLogic;
import me.desht.pneumaticcraft.client.util.TintColor;
import me.desht.pneumaticcraft.common.core.ModTileEntities;
import me.desht.pneumaticcraft.common.heat.HeatUtil;
import me.desht.pneumaticcraft.common.heat.SyncedTemperature;
import me.desht.pneumaticcraft.common.network.DescSynced;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public class TileEntityCompressedIronBlock extends TileEntityTickableBase implements IComparatorSupport, IHeatTinted, IHeatExchangingTE {

    protected final IHeatExchangerLogic heatExchanger = PneumaticRegistry.getInstance().getHeatRegistry().makeHeatExchangerLogic();
    private final LazyOptional<IHeatExchangerLogic> heatCap = LazyOptional.of(() -> heatExchanger);
    private int comparatorOutput = 0;
    @DescSynced
    protected final SyncedTemperature syncedTemperature = new SyncedTemperature(heatExchanger);

    public TileEntityCompressedIronBlock() {
        this(ModTileEntities.COMPRESSED_IRON_BLOCK.get());
    }

    TileEntityCompressedIronBlock(TileEntityType type) {
        super(type);

        heatExchanger.setThermalCapacity(10);
    }

    @Override
    public LazyOptional<IHeatExchangerLogic> getHeatCap(Direction side) {
        return heatCap;
    }

    @Override
    public void tick() {
        super.tick();

        if (!getLevel().isClientSide) {
            syncedTemperature.tick();

            int newComparatorOutput = HeatUtil.getComparatorOutput((int) heatExchanger.getTemperature());
            if (comparatorOutput != newComparatorOutput) {
                comparatorOutput = newComparatorOutput;
                level.updateNeighbourForOutputSignal(getBlockPos(), getBlockState().getBlock());
            }
        }
    }

    @Override
    protected boolean shouldRerenderChunkOnDescUpdate() {
        return true;
    }

    @Override
    public IItemHandler getPrimaryInventory() {
        return null;
    }

    @Override
    public int getComparatorValue() {
        return comparatorOutput;
    }

    @Override
    public TintColor getColorForTintIndex(int tintIndex) {
        return HeatUtil.getColourForTemperature(syncedTemperature.getSyncedTemp());
    }

    @Override
    public IHeatExchangerLogic getHeatExchanger(Direction dir) {
        return heatExchanger;
    }
}
