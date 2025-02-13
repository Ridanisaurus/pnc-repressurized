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

package me.desht.pneumaticcraft.common.progwidgets.area;

import me.desht.pneumaticcraft.common.util.LegacyAreaWidgetConverter;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class AreaTypeRandom extends AreaType {

    public static final String ID = "random";
    private int pickedAmount;

    public AreaTypeRandom() {
        super(ID);
    }

    @Override
    public String toString() {
        return getName() + "/" + pickedAmount;
    }

    @Override
    public void addArea(Consumer<BlockPos> areaAdder, BlockPos p1, BlockPos p2, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        int size = (maxX - minX) * (maxY - minY) * (maxZ - minZ);

        if (pickedAmount >= size) {
            // If we pick >= than there are blocks, just pick all blocks
            BlockPos.betweenClosedStream(minX, minY, minZ, maxX, maxY, maxZ).forEach(pos -> areaAdder.accept(pos.immutable()));
        } else {
            Random rand = ThreadLocalRandom.current();
            for (int i = 0; i < pickedAmount; i++) {
                int x = minX + rand.nextInt(maxX - minX);
                int y = minY + rand.nextInt(maxY - minY);
                int z = minZ + rand.nextInt(maxZ - minZ);
                areaAdder.accept(new BlockPos(x, y, z));
            }
        }
    }

    @Override
    public boolean isDeterministic() {
        return false;
    }

    @Override
    public void addUIWidgets(List<AreaTypeWidget> widgets) {
        super.addUIWidgets(widgets);
        widgets.add(new AreaTypeWidgetInteger("pneumaticcraft.gui.progWidget.area.type.random.blocksSelected", () -> pickedAmount, amount -> pickedAmount = amount));
    }

    @Override
    public void writeToNBT(CompoundNBT tag) {
        super.writeToNBT(tag);
        tag.putInt("pickedAmount", pickedAmount);
    }

    @Override
    public void readFromNBT(CompoundNBT tag) {
        super.readFromNBT(tag);
        pickedAmount = tag.getInt("pickedAmount");
    }

    @Override
    public void writeToPacket(PacketBuffer buffer) {
        super.writeToPacket(buffer);
        buffer.writeVarInt(pickedAmount);
    }

    @Override
    public void readFromPacket(PacketBuffer buf) {
        super.readFromPacket(buf);
        pickedAmount = buf.readVarInt();
    }

    @Override
    public void convertFromLegacy(LegacyAreaWidgetConverter.EnumOldAreaType oldAreaType, int typeInfo) {
        pickedAmount = typeInfo;
    }
}
