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
import me.desht.pneumaticcraft.api.PNCCapabilities;
import me.desht.pneumaticcraft.api.drone.ProgWidgetType;
import me.desht.pneumaticcraft.api.tileentity.IAirHandlerMachine;
import me.desht.pneumaticcraft.common.ai.DroneAIBlockCondition;
import me.desht.pneumaticcraft.common.ai.IDroneBase;
import me.desht.pneumaticcraft.common.core.ModProgWidgets;
import me.desht.pneumaticcraft.common.util.DirectionUtil;
import me.desht.pneumaticcraft.lib.Textures;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class ProgWidgetPressureCondition extends ProgWidgetCondition {

    public ProgWidgetPressureCondition() {
        super(ModProgWidgets.CONDITION_PRESSURE.get());
    }

    @Override
    public List<ProgWidgetType<?>> getParameters() {
        return ImmutableList.of(ModProgWidgets.AREA.get(), ModProgWidgets.TEXT.get());
    }

    @Override
    protected DroneAIBlockCondition getEvaluator(IDroneBase drone, IProgWidget widget) {
        return new DroneAIBlockCondition(drone, (ProgWidgetAreaItemBase) widget) {

            @Override
            protected boolean evaluate(BlockPos pos) {
                TileEntity te = drone.world().getBlockEntity(pos);
                if (te != null) {
                    float pressure = Float.MIN_VALUE;
                    for (Direction d : DirectionUtil.VALUES) {
                        if (getSides()[d.ordinal()]) {
                            float p = te.getCapability(PNCCapabilities.AIR_HANDLER_MACHINE_CAPABILITY, d)
                                    .map(IAirHandlerMachine::getPressure)
                                    .orElse(0f);
                            pressure = Math.max(pressure, p);
                        }
                    }
                    maybeRecordMeasuredVal(drone, (int) (pressure * 1000));
                    return ((ICondition) progWidget).getOperator().evaluate(pressure, ((ICondition) progWidget).getRequiredCount());
                }
                return false;
            }

        };
    }

    @Override
    public ResourceLocation getTexture() {
        return Textures.PROG_WIDGET_CONDITION_PRESSURE;
    }

}
