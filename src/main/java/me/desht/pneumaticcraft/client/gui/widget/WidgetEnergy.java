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

package me.desht.pneumaticcraft.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.desht.pneumaticcraft.lib.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;

public class WidgetEnergy extends Widget implements ITooltipProvider {
    private static final int DEFAULT_SCALE = 42;

    private final IEnergyStorage storage;

    public WidgetEnergy(int x, int y, IEnergyStorage storage) {
        super(x, y, 16, DEFAULT_SCALE, StringTextComponent.EMPTY);
        this.storage = storage;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTick){
        int amount = getScaled();

        Minecraft.getInstance().getTextureManager().bind(Textures.WIDGET_ENERGY);
        AbstractGui.blit(matrixStack, x + 1, y, 1, 0, width - 2, height, 32, 64);
        AbstractGui.blit(matrixStack, x + 1, y + DEFAULT_SCALE - amount, 17, DEFAULT_SCALE - amount, width - 2, amount, 32, 64);
    }

    @Override
    public void addTooltip(double mouseX, double mouseY, List<ITextComponent> list, boolean shiftPressed){
        list.add(new StringTextComponent(storage.getEnergyStored() + " / " + storage.getMaxEnergyStored() + " FE"));
    }

    private int getScaled(){
        if (storage.getMaxEnergyStored() <= 0) {
            return height;
        }
        return storage.getEnergyStored() * height / storage.getMaxEnergyStored();
    }
}
