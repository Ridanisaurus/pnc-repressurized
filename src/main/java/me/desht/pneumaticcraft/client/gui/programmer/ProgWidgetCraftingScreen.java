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

import me.desht.pneumaticcraft.client.gui.ProgrammerScreen;
import me.desht.pneumaticcraft.common.drone.progwidgets.ProgWidgetCrafting;

public class ProgWidgetCraftingScreen extends ProgWidgetImportExportScreen<ProgWidgetCrafting> {
    public ProgWidgetCraftingScreen(ProgWidgetCrafting progWidget, ProgrammerScreen guiProgrammer) {
        super(progWidget, guiProgrammer);
    }

    @Override
    protected String countTooltipKey() {
        return "pneumaticcraft.gui.progWidget.itemFilter.useItemCount.craftingTooltip";
    }

    @Override
    protected boolean showSides() {
        return false;
    }

    public boolean displayShowAreaButtons() {
        return false;
    }
}
