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

import me.desht.pneumaticcraft.common.util.PneumaticCraftUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.function.Consumer;

public class AreaTypeSphere extends AreaType{

    public static final String ID = "sphere";
    
    private EnumSphereType sphereType = EnumSphereType.FILLED;
    
    private enum EnumSphereType{
        FILLED("filled"), HOLLOW("hollow");
        
        private final String name;
        
        EnumSphereType(String name){
            this.name = "pneumaticcraft.gui.progWidget.area.type.sphere.sphereType." + name;
        }
        
        @Override
        public String toString(){
            return I18n.get(name);
        }
    }
    
    public AreaTypeSphere(){
        super(ID);
    }

    @Override
    public String toString() {
        return getName() + "/" + sphereType;
    }

    @Override
    public void addArea(Consumer<BlockPos> areaAdder, BlockPos p1, BlockPos p2, int minX, int minY, int minZ, int maxX, int maxY, int maxZ){
        double radius = PneumaticCraftUtils.distBetween(p1, p2);
        double radiusSq = radius * radius;
        double innerRadius = sphereType == EnumSphereType.HOLLOW ? radius - 1 : 0;
        double innerRadiusSq = innerRadius * innerRadius;
        minX = (int) (p1.getX() - radius - 1);
        minY = (int) (p1.getY() - radius - 1);
        minZ = (int) (p1.getZ() - radius - 1);
        maxX = (int) (p1.getX() + radius + 1);
        maxY = (int) (p1.getY() + radius + 1);
        maxZ = (int) (p1.getZ() + radius + 1);
        for (int x = minX; x <= maxX; x++) {
            for (int y = Math.max(0, minY); y <= maxY && y < 256; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    double centerDistSq = PneumaticCraftUtils.distBetweenSq(p1, x + 0.5, y + 0.5, z + 0.5);
                    if (centerDistSq <= radiusSq && centerDistSq >= innerRadiusSq) { //Only add blocks between a certain radius
                        areaAdder.accept(new BlockPos(x, y, z));
                    }
                }
            }
        }
    }
    
    @Override
    public void addUIWidgets(List<AreaTypeWidget> widgets){
        super.addUIWidgets(widgets);
        widgets.add(new AreaTypeWidgetEnum<>("pneumaticcraft.gui.progWidget.area.type.sphere.sphereType", EnumSphereType.class, () -> sphereType, sphereType -> this.sphereType = sphereType));
    }
    
    @Override
    public void writeToNBT(CompoundNBT tag){
        super.writeToNBT(tag);
        tag.putByte("sphereType", (byte)sphereType.ordinal());
    }

    @Override
    public void writeToPacket(PacketBuffer buffer) {
        super.writeToPacket(buffer);
        buffer.writeByte(sphereType.ordinal());
    }

    @Override
    public void readFromPacket(PacketBuffer buf) {
        super.readFromPacket(buf);
        sphereType = EnumSphereType.values()[buf.readByte()];
    }

    @Override
    public void readFromNBT(CompoundNBT tag){
        super.readFromNBT(tag);
        sphereType = EnumSphereType.values()[tag.getByte("sphereType")];
    }
}
