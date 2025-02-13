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

package me.desht.pneumaticcraft.client.gui.programmer;

import me.desht.pneumaticcraft.client.gui.GuiProgrammer;
import me.desht.pneumaticcraft.client.gui.widget.WidgetCheckBox;
import me.desht.pneumaticcraft.client.gui.widget.WidgetComboBox;
import me.desht.pneumaticcraft.client.gui.widget.WidgetLabel;
import me.desht.pneumaticcraft.client.util.ClientUtils;
import me.desht.pneumaticcraft.client.util.GuiUtils;
import me.desht.pneumaticcraft.common.progwidgets.IBlockRightClicker.RightClickType;
import me.desht.pneumaticcraft.common.progwidgets.ProgWidgetBlockRightClick;
import net.minecraft.util.Direction;

import static me.desht.pneumaticcraft.common.util.PneumaticCraftUtils.xlate;

public class GuiProgWidgetBlockRightClick extends GuiProgWidgetDigAndPlace<ProgWidgetBlockRightClick> {
    private WidgetCheckBox checkboxSneaking;
    private WidgetComboBox sideSelector;
    private WidgetComboBox clickTypeSelector;

    public GuiProgWidgetBlockRightClick(ProgWidgetBlockRightClick widget, GuiProgrammer guiProgrammer) {
        super(widget, guiProgrammer);
    }

    @Override
    public void init() {
        super.init();

        WidgetLabel sideLabel;
        addButton(sideLabel = new WidgetLabel(guiLeft + 8, guiTop + 45, xlate("pneumaticcraft.gui.progWidget.blockRightClick.clickSide")));

        sideSelector = new WidgetComboBox(font, guiLeft + 8 + sideLabel.getWidth() + 5, guiTop + 43, 50, 12)
                .initFromEnum(progWidget.getClickSide(), ClientUtils::translateDirection);
        addButton(sideSelector);

        WidgetLabel opLabel;
        addButton(opLabel = new WidgetLabel(guiLeft + 8, guiTop + 65,
                xlate("pneumaticcraft.gui.progWidget.blockRightClick.operation")));

        clickTypeSelector = new WidgetComboBox(font, guiLeft + 8 + opLabel.getWidth() + 5, guiTop + 63, 80, 12)
                .initFromEnum(progWidget.getClickType());
        clickTypeSelector.setTooltip(GuiUtils.xlateAndSplit("pneumaticcraft.gui.progWidget.blockRightClick.clickType.tooltip"));
        addButton(clickTypeSelector);

        checkboxSneaking = new WidgetCheckBox(guiLeft + 8, guiTop + 83, 0xFF404040,
                xlate("pneumaticcraft.gui.progWidget.blockRightClick.sneaking"))
                .setChecked(progWidget.isSneaking())
                .setTooltipKey("pneumaticcraft.gui.progWidget.blockRightClick.sneaking.tooltip");
        addButton(checkboxSneaking);
    }

    @Override
    public void removed() {
        if (sideSelector.getSelectedElementIndex() >= 0) {
            progWidget.setClickSide(Direction.from3DDataValue(sideSelector.getSelectedElementIndex()));
        }
        if (clickTypeSelector.getSelectedElementIndex() >= 0) {
            progWidget.setClickType(RightClickType.values()[clickTypeSelector.getSelectedElementIndex()]);
        }
        progWidget.setSneaking(checkboxSneaking.checked);

        super.removed();
    }
}
