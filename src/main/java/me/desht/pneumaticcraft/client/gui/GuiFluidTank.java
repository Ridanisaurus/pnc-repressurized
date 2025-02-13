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

import me.desht.pneumaticcraft.client.gui.widget.WidgetTank;
import me.desht.pneumaticcraft.common.inventory.ContainerFluidTank;
import me.desht.pneumaticcraft.common.tileentity.TileEntityFluidTank;
import me.desht.pneumaticcraft.lib.Textures;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GuiFluidTank extends GuiPneumaticContainerBase<ContainerFluidTank, TileEntityFluidTank> {
    public GuiFluidTank(ContainerFluidTank container, PlayerInventory inv, ITextComponent displayString) {
        super(container, inv, displayString);
    }

    @Override
    public void init() {
        super.init();

        addButton(new WidgetTank(leftPos + 152, topPos + 15, te.getTank()));
    }

    @Override
    protected String upgradeCategory() {
        return "fluid_tank";
    }

    @Override
    protected ResourceLocation getGuiTexture() {
        return Textures.GUI_FLUID_TANK;
    }

    @Override
    protected boolean shouldAddProblemTab() {
        return false;
    }
}
