/*
 * This file is part of pnc-repressurized API.
 *
 *     pnc-repressurized API is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     pnc-repressurized is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with pnc-repressurized API.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.desht.pneumaticcraft.api.crafting.recipe;

import me.desht.pneumaticcraft.api.crafting.TemperatureRange;
import me.desht.pneumaticcraft.api.crafting.ingredient.FluidIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public abstract class ThermoPlantRecipe extends PneumaticCraftRecipe {
    protected ThermoPlantRecipe(ResourceLocation id) {
        super(id);
    }

    /**
     * Check if this recipe matches the given input fluid and item.  This does not take any required temperature and
     * pressure into account.  It will also match if the input fluid matches but is insufficient.
     *
     * @param inputFluid the input fluid
     * @param inputItem the input item
     * @return true if this recipe matches
     */
    public abstract boolean matches(FluidStack inputFluid, ItemStack inputItem);

    /**
     * Get the temperature range required for processing to occur.
     *
     * @return temperature range, in degrees Kelvin.
     */
    public abstract TemperatureRange getOperatingTemperature();

    /**
     * Get the minimum pressure required for processing to occur.
     *
     * @return pressure, in bar.
     */
    public abstract float getRequiredPressure();

    /**
     * Get the base heat used each time the processing plant produces some output.  This value will be subtracted from
     * the machine's current heat.  This could be negative if the recipe is an exothermic recipe, i.e. it produces
     * heat; see {@link #isExothermic()}.
     *
     * @param ambientTemperature the machine's ambient temperature
     * @return heat used
     */
    public double heatUsed(double ambientTemperature) {
        TemperatureRange range = getOperatingTemperature();
        if (range.isAny()) return 0;  // don't care about temperature; don't consume or produce heat

        double used;
        if (range.getMin() > ambientTemperature) {
            used = (range.getMin() - ambientTemperature) / 10D;
        } else if (range.getMax() < ambientTemperature) {
            used = (ambientTemperature - range.getMax()) / 10D;
        } else {
            if (isExothermic()) {
                used = (range.getMax() - ambientTemperature) / 10D;
            } else {
                used = (ambientTemperature - range.getMin()) / 10D;
            }
        }
        return isExothermic() ? -used : used;
    }

    /**
     * Get the base air used each time the processing plant produces some output.  By default, this is 50mL of air per
     * bar of pressure required.
     *
     * @return air used
     */
    public int airUsed() {
        return (int) (50 * getRequiredPressure());
    }

    public abstract Ingredient getInputItem();

    public abstract FluidIngredient getInputFluid();

    public abstract FluidStack getOutputFluid();

    public abstract ItemStack getOutputItem();

    public abstract double getRecipeSpeed();

    /**
     * Check if this recipe is exothermic, i.e. produces heat rather than requiring it. Such recipes generally
     * have a maximum temperature defined, instead of (or as well as) a minimum temperature.
     *
     * @return true if this is an exothermic recipe.
     */
    public abstract boolean isExothermic();
}
