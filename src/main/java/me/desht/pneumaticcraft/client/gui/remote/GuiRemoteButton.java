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

package me.desht.pneumaticcraft.client.gui.remote;

import me.desht.pneumaticcraft.client.gui.GuiRemoteEditor;
import me.desht.pneumaticcraft.client.gui.remote.actionwidget.ActionWidgetButton;
import me.desht.pneumaticcraft.client.gui.widget.WidgetTextFieldNumber;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import static me.desht.pneumaticcraft.common.util.PneumaticCraftUtils.xlate;

public class GuiRemoteButton extends GuiRemoteVariable<ActionWidgetButton> {
    private WidgetTextFieldNumber widthField;
    private WidgetTextFieldNumber heightField;
    private WidgetTextFieldNumber xValueField, yValueField, zValueField;

    public GuiRemoteButton(ActionWidgetButton widget, GuiRemoteEditor guiRemote) {
        super(widget, guiRemote);
    }

    @Override
    public void init() {
        super.init();

        addLabel(xlate("pneumaticcraft.gui.remote.button.settingValue"), guiLeft + 10, guiTop + 95);
        addLabel(new StringTextComponent("X:"), guiLeft + 10, guiTop + 106);
        addLabel(new StringTextComponent("Y:"), guiLeft + 67, guiTop + 106);
        addLabel(new StringTextComponent("Z:"), guiLeft + 124, guiTop + 106);
        addLabel(xlate("pneumaticcraft.gui.remote.button.width"), guiLeft + 10, guiTop + 123);
        addLabel(xlate("pneumaticcraft.gui.remote.button.height"), guiLeft + 10, guiTop + 138);

        ITextComponent valueTooltip = xlate("pneumaticcraft.gui.remote.button.value.tooltip");

        xValueField = new WidgetTextFieldNumber(font, guiLeft + 20, guiTop + 105, 38, 10);
        xValueField.setValue(actionWidget.settingCoordinate.getX());
        xValueField.setTooltip(valueTooltip);
        addButton(xValueField);

        yValueField = new WidgetTextFieldNumber(font, guiLeft + 78, guiTop + 105, 38, 10);
        yValueField.setValue(actionWidget.settingCoordinate.getY());
        yValueField.setTooltip(valueTooltip);
        addButton(yValueField);

        zValueField = new WidgetTextFieldNumber(font, guiLeft + 136, guiTop + 105, 38, 10);
        zValueField.setValue(actionWidget.settingCoordinate.getZ());
        zValueField.setTooltip(valueTooltip);
        addButton(zValueField);

        widthField = new WidgetTextFieldNumber(font, guiLeft + 100, guiTop + 123, 60, 10)
                .setRange(10, Integer.MAX_VALUE).setAdjustments(1, 10);
        widthField.setValue(actionWidget.getWidth());
        widthField.minValue = 10;
        addButton(widthField);

        heightField = new WidgetTextFieldNumber(font, guiLeft + 100, guiTop + 138, 60, 10)
                .setRange(10, Integer.MAX_VALUE).setAdjustments(1, 10);
        heightField.setValue(actionWidget.getHeight());
        heightField.minValue = 10;
        heightField.maxValue = 20;
        addButton(heightField);

    }

    @Override
    public void removed() {
        actionWidget.settingCoordinate = new BlockPos(xValueField.getIntValue(), yValueField.getIntValue(), zValueField.getIntValue());
        actionWidget.setWidth(widthField.getIntValue());
        actionWidget.setHeight(heightField.getIntValue());

        super.removed();
    }
}
