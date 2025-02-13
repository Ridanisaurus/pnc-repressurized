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

import me.desht.pneumaticcraft.client.gui.widget.WidgetAnimatedStat;
import me.desht.pneumaticcraft.client.util.ClientUtils;
import me.desht.pneumaticcraft.client.util.GuiUtils;
import me.desht.pneumaticcraft.common.core.ModBlocks;
import me.desht.pneumaticcraft.common.inventory.ContainerElectrostaticCompressor;
import me.desht.pneumaticcraft.common.tileentity.TileEntityElectrostaticCompressor;
import me.desht.pneumaticcraft.common.util.PneumaticCraftUtils;
import me.desht.pneumaticcraft.lib.PneumaticValues;
import me.desht.pneumaticcraft.lib.Textures;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static me.desht.pneumaticcraft.common.util.PneumaticCraftUtils.xlate;

public class GuiElectrostaticCompressor extends GuiPneumaticContainerBase<ContainerElectrostaticCompressor,TileEntityElectrostaticCompressor> {
    private int connectedCompressors;
    private WidgetAnimatedStat electrostaticStat;

    public GuiElectrostaticCompressor(ContainerElectrostaticCompressor container, PlayerInventory inv, ITextComponent displayString) {
        super(container, inv, displayString);
    }

    @Override
    public void init() {
        super.init();
        electrostaticStat = addAnimatedStat(xlate("pneumaticcraft.gui.tab.info.electrostaticCompressor.title"), new ItemStack(ModBlocks.ELECTROSTATIC_COMPRESSOR.get()), 0xFF20A0FF, false);
        electrostaticStat.setForegroundColor(0xFF000000);
    }

    @Override
    protected ResourceLocation getGuiTexture() {
        return Textures.GUI_4UPGRADE_SLOTS;
    }

    @Override
    protected void addWarnings(List<ITextComponent> textList) {
        super.addWarnings(textList);
        int grounding = PneumaticValues.MAX_REDIRECTION_PER_IRON_BAR * te.ironBarsBeneath;
        if (connectedCompressors > 0) {
            int generated = PneumaticValues.PRODUCTION_ELECTROSTATIC_COMPRESSOR / connectedCompressors;
            if (grounding < generated) {
                textList.addAll(GuiUtils.xlateAndSplit("pneumaticcraft.gui.tab.problems.electrostatic.notEnoughGrounding", grounding, generated));
            }
        }
    }

    @Override
    public void tick() {
        if (firstUpdate || ClientUtils.getClientWorld().getGameTime() % 20 == 0) {
            Set<BlockPos> positions = new HashSet<>();
            Set<TileEntityElectrostaticCompressor> compressors = new HashSet<>();
            positions.add(te.getBlockPos());
            te.getElectrostaticGrid(positions, compressors, te.getBlockPos());
            connectedCompressors = compressors.size();
        }

        super.tick();

        List<ITextComponent> info = new ArrayList<>();
        if (connectedCompressors > 0) {
            // should always be the case...
            info.add(xlate("pneumaticcraft.gui.tab.info.electrostatic.generating",
                    PneumaticCraftUtils.roundNumberTo(PneumaticValues.PRODUCTION_ELECTROSTATIC_COMPRESSOR / (float) connectedCompressors, 1)));
        }
        info.add(xlate("pneumaticcraft.gui.tab.info.electrostatic.connected", connectedCompressors));
        info.add(xlate("pneumaticcraft.gui.tab.info.electrostatic.maxRedirection",
                PneumaticCraftUtils.roundNumberTo(PneumaticValues.MAX_REDIRECTION_PER_IRON_BAR * te.ironBarsBeneath, 1)));
        info.add(xlate("pneumaticcraft.gui.tab.info.electrostatic.lightningRod", te.ironBarsAbove));
        info.add(xlate("pneumaticcraft.gui.tab.info.electrostatic.strikeTime",
                PneumaticCraftUtils.convertTicksToMinutesAndSeconds(te.getStrikeChance(), false)));

        electrostaticStat.setText(info);
    }
}
