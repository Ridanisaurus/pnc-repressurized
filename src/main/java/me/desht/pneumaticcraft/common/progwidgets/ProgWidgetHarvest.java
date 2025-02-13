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

package me.desht.pneumaticcraft.common.progwidgets;

import me.desht.pneumaticcraft.common.ai.DroneAIHarvest;
import me.desht.pneumaticcraft.common.ai.IDroneBase;
import me.desht.pneumaticcraft.common.core.ModProgWidgets;
import me.desht.pneumaticcraft.lib.Textures;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

import static me.desht.pneumaticcraft.common.util.PneumaticCraftUtils.xlate;

public class ProgWidgetHarvest extends ProgWidgetDigAndPlace implements IToolUser {

    private boolean requireHoe;
    
    public ProgWidgetHarvest() {
        super(ModProgWidgets.HARVEST.get(), Ordering.CLOSEST);
    }

    @Override
    public ResourceLocation getTexture() {
        return Textures.PROG_WIDGET_HARVEST;
    }

    @Override
    public Goal getWidgetAI(IDroneBase drone, IProgWidget widget) {
        return setupMaxActions(new DroneAIHarvest(drone, (ProgWidgetAreaItemBase) widget), (IMaxActions) widget);
    }

    @Override
    public DyeColor getColor() {
        return DyeColor.BROWN;
    }

    @Override
    public boolean requiresTool(){
        return requireHoe;
    }
    
    @Override
    public void setRequiresTool(boolean requireHoe){
        this.requireHoe = requireHoe;
    }
    
    @Override
    public void getTooltip(List<ITextComponent> curTooltip) {
        super.getTooltip(curTooltip);
        
        if (requiresTool()) {
            curTooltip.add(xlate("pneumaticcraft.gui.progWidget.harvest.requiresHoe"));
        }
    }
    
    @Override
    public void writeToNBT(CompoundNBT tag){
        super.writeToNBT(tag);
        if (requireHoe) tag.putBoolean("requireHoe", true);
    }
    
    @Override
    public void readFromNBT(CompoundNBT tag){
        super.readFromNBT(tag);
        requireHoe = tag.getBoolean("requireHoe");
    }

    @Override
    public void writeToPacket(PacketBuffer buf) {
        super.writeToPacket(buf);
        buf.writeBoolean(requireHoe);
    }

    @Override
    public void readFromPacket(PacketBuffer buf) {
        super.readFromPacket(buf);
        requireHoe = buf.readBoolean();
    }
}
