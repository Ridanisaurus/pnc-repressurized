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

package me.desht.pneumaticcraft.common.block.tubes;

import me.desht.pneumaticcraft.common.item.ItemTubeModule;
import me.desht.pneumaticcraft.common.util.PneumaticCraftUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public abstract class TubeModuleRedstoneReceiving extends TubeModule {
    private int redstoneLevel;

    TubeModuleRedstoneReceiving(ItemTubeModule item) {
        super(item);
    }

    @Override
    public void readFromNBT(CompoundNBT tag) {
        super.readFromNBT(tag);
        redstoneLevel = tag.getInt("redstone");
    }

    @Override
    public CompoundNBT writeToNBT(CompoundNBT tag) {
        super.writeToNBT(tag);
        tag.putInt("redstone", redstoneLevel);
        return tag;
    }

    @Override
    public void addInfo(List<ITextComponent> curInfo) {
        super.addInfo(curInfo);

        curInfo.add(PneumaticCraftUtils.xlate("pneumaticcraft.waila.redstoneModule.receiving", redstoneLevel));
        curInfo.add(PneumaticCraftUtils.xlate("pneumaticcraft.waila.tubeModule.threshold", PneumaticCraftUtils.roundNumberTo(getThreshold(), 1)));
    }

    @Override
    public void onNeighborBlockUpdate() {
        redstoneLevel = pressureTube.getLevel().getBestNeighborSignal(pressureTube.getBlockPos());
    }

    public int getReceivingRedstoneLevel() {
        return redstoneLevel;
    }

    public float getThreshold() {
        return getThreshold(redstoneLevel);
    }

    @Override
    public boolean hasGui() {
        return upgraded;
    }

    @Override
    public void update() {
        if (upgraded && !advancedConfig && higherBound != lowerBound) {
            higherBound = lowerBound;
            if (!getTube().getLevel().isClientSide) sendDescriptionPacket();
        }
    }
}
