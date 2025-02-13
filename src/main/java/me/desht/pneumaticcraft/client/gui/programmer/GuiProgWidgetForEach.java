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
import me.desht.pneumaticcraft.client.gui.widget.WidgetComboBox;
import me.desht.pneumaticcraft.common.progwidgets.IProgWidget;
import me.desht.pneumaticcraft.common.progwidgets.IVariableSetWidget;
import me.desht.pneumaticcraft.common.variables.GlobalVariableManager;

import static me.desht.pneumaticcraft.common.util.PneumaticCraftUtils.xlate;

public class GuiProgWidgetForEach<W extends IProgWidget & IVariableSetWidget> extends GuiProgWidgetAreaShow<W> {

    private WidgetComboBox variableField;

    public GuiProgWidgetForEach(W progWidget, GuiProgrammer guiProgrammer) {
        super(progWidget, guiProgrammer);
    }

    @Override
    public void init() {
        super.init();

        variableField = new WidgetComboBox(font, guiLeft + 10, guiTop + 42, 160, font.lineHeight + 1);
        variableField.setElements(guiProgrammer.te.getAllVariables());
        variableField.setMaxLength(GlobalVariableManager.MAX_VARIABLE_LEN);
        addButton(variableField);
        variableField.setValue(progWidget.getVariable());
        variableField.setFocus(true);

        addLabel(xlate("pneumaticcraft.gui.progWidget.coordinate.variableName"), guiLeft + 10, guiTop + 30);
    }

    @Override
    public void removed() {
        progWidget.setVariable(variableField.getValue());

        super.removed();
    }
}
