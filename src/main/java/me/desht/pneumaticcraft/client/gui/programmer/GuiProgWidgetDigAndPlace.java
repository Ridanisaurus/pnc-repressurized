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
import me.desht.pneumaticcraft.client.gui.widget.WidgetTextFieldNumber;
import me.desht.pneumaticcraft.common.progwidgets.IBlockOrdered.Ordering;
import me.desht.pneumaticcraft.common.progwidgets.ProgWidgetDigAndPlace;

import static me.desht.pneumaticcraft.common.util.PneumaticCraftUtils.xlate;

public abstract class GuiProgWidgetDigAndPlace<P extends ProgWidgetDigAndPlace> extends GuiProgWidgetAreaShow<P> {

    private WidgetTextFieldNumber textField;
    private WidgetComboBox orderSelector;

    public GuiProgWidgetDigAndPlace(P progWidget, GuiProgrammer guiProgrammer) {
        super(progWidget, guiProgrammer);
    }

    @Override
    public void init() {
        super.init();

        WidgetLabel orderLabel = new WidgetLabel(guiLeft + 8, guiTop + 25, xlate("pneumaticcraft.gui.progWidget.digAndPlace.order"));
        addButton(orderLabel);

        orderSelector = new WidgetComboBox(font,guiLeft + 8 + orderLabel.getWidth() + 5, guiTop + 23, 80, 12)
                .initFromEnum(progWidget.getOrder());
        addButton(orderSelector);

        if (progWidget.supportsMaxActions()) {
            WidgetCheckBox useMaxActions = new WidgetCheckBox(guiLeft + 8, guiTop + 115, 0xFF404040,
                    xlate("pneumaticcraft.gui.progWidget.digAndPlace.useMaxActions"), b -> {
                progWidget.setUseMaxActions(b.checked);
                textField.setVisible(progWidget.useMaxActions());
            })
                    .setTooltipKey("pneumaticcraft.gui.progWidget.digAndPlace.useMaxActions.tooltip")
                    .setChecked(progWidget.useMaxActions());
            addButton(useMaxActions);

            textField = new WidgetTextFieldNumber(font, guiLeft + 20, guiTop + 128, 30, 11)
                    .setRange(1, Integer.MAX_VALUE)
                    .setAdjustments(1, 10);
            textField.setValue(progWidget.getMaxActions());
            textField.setVisible(useMaxActions.checked);
            addButton(textField);
        }
    }

    @Override
    public void removed() {
        if (orderSelector.getSelectedElementIndex() >= 0) {
            progWidget.setOrder(Ordering.values()[orderSelector.getSelectedElementIndex()]);
        }
        if (progWidget.supportsMaxActions()) {
            progWidget.setMaxActions(textField.getIntValue());
        }

        super.removed();
    }
}
