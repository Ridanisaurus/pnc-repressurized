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

package me.desht.pneumaticcraft.client.render.pneumatic_armor.upgrade_handler;

import com.google.common.base.Strings;
import me.desht.pneumaticcraft.api.client.IGuiAnimatedStat;
import me.desht.pneumaticcraft.api.client.pneumatic_helmet.IArmorUpgradeClientHandler;
import me.desht.pneumaticcraft.api.client.pneumatic_helmet.IGuiScreen;
import me.desht.pneumaticcraft.api.client.pneumatic_helmet.IOptionPage;
import me.desht.pneumaticcraft.api.pneumatic_armor.ICommonArmorHandler;
import me.desht.pneumaticcraft.client.gui.pneumatic_armor.option_screens.AirConditionerOptions;
import me.desht.pneumaticcraft.client.gui.widget.WidgetAnimatedStat;
import me.desht.pneumaticcraft.client.render.pneumatic_armor.HUDHandler;
import me.desht.pneumaticcraft.common.config.subconfig.ArmorHUDLayout;
import me.desht.pneumaticcraft.common.pneumatic_armor.ArmorUpgradeRegistry;
import me.desht.pneumaticcraft.common.pneumatic_armor.handlers.AirConHandler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class AirConClientHandler extends IArmorUpgradeClientHandler.SimpleToggleableHandler<AirConHandler> {
    private static final int MAX_AC = 20;

    public static int deltaTemp;  // set by packet from server
    private static int currentAC = 0; // cosmetic

    private IGuiAnimatedStat acStat;

    public AirConClientHandler() {
        super(ArmorUpgradeRegistry.getInstance().airConHandler);
    }

    @Override
    public IOptionPage getGuiOptionsPage(IGuiScreen screen) {
        return new AirConditionerOptions(screen, this);
    }

    @Override
    public void tickClient(ICommonArmorHandler armorHandler) {
        super.tickClient(armorHandler);

        if ((armorHandler.getPlayer().level.getGameTime() & 0x3) == 0) {
            if (currentAC < deltaTemp)
                currentAC++;
            else if (currentAC > deltaTemp)
                currentAC--;
        }

        if (acStat.isStatOpen()) {
            int ac = MathHelper.clamp(currentAC, -MAX_AC, MAX_AC);
            String bar = (ac < 0 ? TextFormatting.BLUE : TextFormatting.GOLD)
                    + Strings.repeat("|", Math.abs(ac))
                    + TextFormatting.DARK_GRAY
                    + Strings.repeat("|", MAX_AC - Math.abs(ac));
            acStat.setTitle(new StringTextComponent("A/C: " + bar).withStyle(TextFormatting.YELLOW));
            acStat.setBackgroundColor(ac < 0 ? 0x300080FF : (ac == 0 ? 0x3000AA00 : 0x30FFD000));
        }
    }

    @Override
    public IGuiAnimatedStat getAnimatedStat() {
        if (acStat == null) {
            acStat = new WidgetAnimatedStat(null, StringTextComponent.EMPTY, WidgetAnimatedStat.StatIcon.NONE,
                    HUDHandler.getInstance().getStatOverlayColor(), null, ArmorHUDLayout.INSTANCE.airConStat);
            acStat.setMinimumContractedDimensions(0, 0);
        }
        return acStat;
    }

    @Override
    public void onResolutionChanged() {
        acStat = null;
    }
}
