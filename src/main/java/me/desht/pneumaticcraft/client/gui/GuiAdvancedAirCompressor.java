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

package me.desht.pneumaticcraft.client.gui;

import me.desht.pneumaticcraft.api.crafting.TemperatureRange;
import me.desht.pneumaticcraft.client.gui.widget.WidgetTemperature;
import me.desht.pneumaticcraft.client.util.GuiUtils;
import me.desht.pneumaticcraft.client.util.PointXY;
import me.desht.pneumaticcraft.common.inventory.ContainerAirCompressor;
import me.desht.pneumaticcraft.common.tileentity.TileEntityAdvancedAirCompressor;
import me.desht.pneumaticcraft.lib.Textures;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class GuiAdvancedAirCompressor extends GuiAirCompressor {
    private WidgetTemperature tempWidget;

    public GuiAdvancedAirCompressor(ContainerAirCompressor container, PlayerInventory inv, ITextComponent displayString) {
        super(container, inv, displayString);
    }

    @Override
    public void init() {
        super.init();

        addButton(tempWidget = new WidgetTemperature(leftPos + 97, topPos + 20, TemperatureRange.of(273, 673), 273, 50)
                .setOperatingRange(TemperatureRange.of(323, 625)).setShowOperatingRange(false));
    }

    @Override
    public void tick() {
        super.tick();

        tempWidget.setTemperature(((TileEntityAdvancedAirCompressor) te).getHeatExchanger().getTemperatureAsInt());
        tempWidget.autoScaleForTemperature();
    }

    @Override
    protected int getFuelSlotXOffset() {
        return 69;
    }

    @Override
    public void addWarnings(List<ITextComponent> curInfo) {
        super.addWarnings(curInfo);
        if (te.getHeatEfficiency() < 100) {
            curInfo.addAll(GuiUtils.xlateAndSplit("pneumaticcraft.gui.tab.problems.advancedAirCompressor.efficiency", te.getHeatEfficiency() + "%"));
        }
    }

    @Override
    protected PointXY getGaugeLocation() {
        return super.getGaugeLocation().add(10, 0);
    }

    @Override
    protected ResourceLocation getGuiTexture() {
        return Textures.GUI_ADVANCED_AIR_COMPRESSOR;
    }
}
