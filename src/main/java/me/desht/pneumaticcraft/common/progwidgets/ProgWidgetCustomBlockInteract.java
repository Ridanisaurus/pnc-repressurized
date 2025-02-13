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
import me.desht.pneumaticcraft.api.drone.ICustomBlockInteract;
import me.desht.pneumaticcraft.api.drone.ProgWidgetType;
import me.desht.pneumaticcraft.common.ai.DroneAICustomBlockInteract;
import me.desht.pneumaticcraft.common.ai.IDroneBase;
import me.desht.pneumaticcraft.common.core.ModProgWidgets;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;

import java.util.List;

public class ProgWidgetCustomBlockInteract extends ProgWidgetInventoryBase {
    private ICustomBlockInteract interactor;
    private ProgWidgetType<?> customType = null;

    public ProgWidgetCustomBlockInteract() {
        super(null);
    }

    public ProgWidgetCustomBlockInteract setInteractor(ICustomBlockInteract interactor) {
        this.interactor = interactor;
        return this;
    }

    @Override
    public ProgWidgetType<?> getType() {
        if (customType == null) {
            customType = ModProgWidgets.PROG_WIDGETS.get().getValue(interactor.getID());
            Validate.notNull(customType);
        }
        return customType;
    }

    @Override
    public IProgWidget copy() {
        ProgWidgetCustomBlockInteract widget = (ProgWidgetCustomBlockInteract) super.copy();
        widget.setInteractor(interactor);
        return widget;
    }

    @Override
    public ResourceLocation getTexture() {
        return interactor.getTexture();
    }

    @Override
    public Goal getWidgetAI(IDroneBase drone, IProgWidget widget) {
        return new DroneAICustomBlockInteract(drone, (ProgWidgetInventoryBase) widget, interactor);
    }

    @Override
    public List<ProgWidgetType<?>> getParameters() {
        return ImmutableList.of(ModProgWidgets.AREA.get());
    }

    @Override
    public DyeColor getColor() {
        return interactor.getColor();
    }

}
