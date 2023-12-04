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

package me.desht.pneumaticcraft.common.drone.progwidgets.area;

import me.desht.pneumaticcraft.common.util.LegacyAreaWidgetConverter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;
import java.util.function.Consumer;

public class AreaTypeGrid extends AreaType{

    public static final String ID = "grid";
    private int interval;
    
    public AreaTypeGrid(){
        super(ID);
    }

    @Override
    public String toString() {
        return getName() + "/" + interval;
    }

    @Override
    public void addArea(Consumer<BlockPos> areaAdder, BlockPos p1, BlockPos p2, int minX, int minY, int minZ, int maxX, int maxY, int maxZ){
        if (p1.equals(p2) || interval <= 0) {
            areaAdder.accept(p1);
        } else {
            for (int x = minX; x <= maxX; x += interval) {
                for (int y = minY; y <= maxY; y += interval) {
                    for (int z = minZ; z <= maxZ; z += interval) {
                        areaAdder.accept(new BlockPos(x, y, z));
                    }
                }
            }
        }
    }
    
    @Override
    public void addUIWidgets(List<AreaTypeWidget> widgets){
        super.addUIWidgets(widgets);
        widgets.add(new AreaTypeWidgetInteger("pneumaticcraft.gui.progWidget.area.type.grid.interval", () -> interval, interval -> this.interval = interval));
    }
    
    @Override
    public void writeToNBT(CompoundTag tag){
        super.writeToNBT(tag);
        tag.putInt("interval", interval);
    }
    
    @Override
    public void readFromNBT(CompoundTag tag){
        super.readFromNBT(tag);
        interval = tag.getInt("interval");
    }

    @Override
    public void writeToPacket(FriendlyByteBuf buffer) {
        super.writeToPacket(buffer);
        buffer.writeVarInt(interval);
    }

    @Override
    public void readFromPacket(FriendlyByteBuf buf) {
        super.readFromPacket(buf);
        interval = buf.readVarInt();
    }

    @Override
    public void convertFromLegacy(LegacyAreaWidgetConverter.EnumOldAreaType oldAreaType, int typeInfo){
        interval = typeInfo;
    }
}
