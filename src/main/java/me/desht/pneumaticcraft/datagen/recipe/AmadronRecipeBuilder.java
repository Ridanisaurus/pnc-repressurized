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

package me.desht.pneumaticcraft.datagen.recipe;

import com.google.gson.JsonObject;
import me.desht.pneumaticcraft.api.crafting.AmadronTradeResource;
import me.desht.pneumaticcraft.api.crafting.PneumaticCraftRecipeTypes;
import me.desht.pneumaticcraft.common.recipes.amadron.AmadronOffer;
import me.desht.pneumaticcraft.common.util.PlayerFilter;
import net.minecraft.resources.ResourceLocation;

import static me.desht.pneumaticcraft.api.PneumaticRegistry.RL;

public class AmadronRecipeBuilder extends PneumaticCraftRecipeBuilder<AmadronRecipeBuilder> {
    private final AmadronTradeResource input;
    private final AmadronTradeResource output;
    private final boolean isStatic;
    private final int level;
    private final int maxStock;
    private final PlayerFilter whitelist;
    private final PlayerFilter blacklist;

    public AmadronRecipeBuilder(AmadronTradeResource input, AmadronTradeResource output, boolean isStatic, int level,
                                int maxStock, PlayerFilter whitelist, PlayerFilter blacklist) {
        super(RL(PneumaticCraftRecipeTypes.AMADRON_OFFERS));
        this.input = input;
        this.output = output;
        this.isStatic = isStatic;
        this.level = level;
        this.maxStock = maxStock;
        this.whitelist = whitelist;
        this.blacklist = blacklist;
    }

    public AmadronRecipeBuilder(AmadronTradeResource input, AmadronTradeResource output, boolean isStatic, int level) {
        this(input, output, isStatic, level, -1, PlayerFilter.YES, PlayerFilter.NO);
    }

    @Override
    protected RecipeResult getResult(ResourceLocation id) {
        return new AmadronRecipeResult(id);
    }

    public class AmadronRecipeResult extends RecipeResult {
        AmadronRecipeResult(ResourceLocation id) {
            super(id);
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            new AmadronOffer(getId(), input, output, isStatic, level, maxStock, maxStock, whitelist, blacklist).toJson(json);
        }
    }
}
