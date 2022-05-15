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

package me.desht.pneumaticcraft.common.pneumatic_armor.handlers;

import me.desht.pneumaticcraft.api.item.PNCUpgrade;
import me.desht.pneumaticcraft.api.pneumatic_armor.BaseArmorUpgradeHandler;
import me.desht.pneumaticcraft.api.pneumatic_armor.IArmorExtensionData;
import me.desht.pneumaticcraft.api.pneumatic_armor.ICommonArmorHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;

import static me.desht.pneumaticcraft.api.PneumaticRegistry.RL;

public class StepAssistHandler extends BaseArmorUpgradeHandler<IArmorExtensionData> {
    private static final ResourceLocation ID = RL("step_assist");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PNCUpgrade[] getRequiredUpgrades() {
        return new PNCUpgrade[0];  // no upgrades needed, boots built-in
    }

    @Override
    public float getIdleAirUsage(ICommonArmorHandler armorHandler) {
        return 0;
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.FEET;
    }

    @Override
    public void tick(ICommonArmorHandler commonArmorHandler, boolean enabled) {
        // we will give the player a step height boost every tick if enabled
        // but we won't take it away here, since that could mess up items from other
        // mods which grant step assist
        if (commonArmorHandler.hasMinPressure(EquipmentSlot.FEET) && enabled) {
            Player player = commonArmorHandler.getPlayer();
            player.maxUpStep = player.isShiftKeyDown() ? 0.6001F : 1.25F;
        }
    }

    @Override
    public void onToggle(ICommonArmorHandler commonArmorHandler, boolean newState) {
        if (!newState) {
            commonArmorHandler.getPlayer().maxUpStep = 0.6F;
        }
    }

    @Override
    public void onShutdown(ICommonArmorHandler commonArmorHandler) {
        commonArmorHandler.getPlayer().maxUpStep = 0.6F;
    }
}
