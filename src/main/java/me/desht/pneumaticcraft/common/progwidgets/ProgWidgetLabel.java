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

import com.google.common.collect.ImmutableList;
import me.desht.pneumaticcraft.api.drone.ProgWidgetType;
import me.desht.pneumaticcraft.common.core.ModProgWidgets;
import me.desht.pneumaticcraft.lib.Textures;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

import static me.desht.pneumaticcraft.common.util.PneumaticCraftUtils.xlate;

public class ProgWidgetLabel extends ProgWidget implements ILabel {

    public ProgWidgetLabel() {
        super(ModProgWidgets.LABEL.get());
    }

    @Override
    public void addErrors(List<ITextComponent> curInfo, List<IProgWidget> widgets) {
        super.addErrors(curInfo, widgets);
        if (getConnectedParameters()[0] == null) curInfo.add(xlate("pneumaticcraft.gui.progWidget.label.error.noLabel"));
    }

    @Override
    public boolean hasStepInput() {
        return false;
    }

    @Override
    public boolean hasStepOutput() {
        return true;
    }

    @Override
    public ProgWidgetType<?> returnType() {
        return null;
    }

    @Override
    public List<ProgWidgetType<?>> getParameters() {
        return ImmutableList.of(ModProgWidgets.TEXT.get());
    }

    @Override
    protected boolean hasBlacklist() {
        return false;
    }

    @Override
    public ResourceLocation getTexture() {
        return Textures.PROG_WIDGET_LABEL;
    }

    @Override
    public WidgetDifficulty getDifficulty() {
        return WidgetDifficulty.MEDIUM;
    }

    @Override
    public String getLabel() {
        ProgWidgetText labelWidget = (ProgWidgetText) getConnectedParameters()[0];
        return labelWidget != null ? labelWidget.string : null;
    }

    @Override
    public DyeColor getColor() {
        return DyeColor.WHITE;
    }
}
