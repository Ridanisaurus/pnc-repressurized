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
import me.desht.pneumaticcraft.api.misc.Symbols;
import me.desht.pneumaticcraft.common.ai.DroneAIManager;
import me.desht.pneumaticcraft.common.core.ModItems;
import me.desht.pneumaticcraft.common.core.ModProgWidgets;
import me.desht.pneumaticcraft.common.item.ItemTagFilter;
import me.desht.pneumaticcraft.common.thirdparty.ModNameCache;
import me.desht.pneumaticcraft.common.util.PneumaticCraftUtils;
import me.desht.pneumaticcraft.common.variables.GlobalVariableManager;
import me.desht.pneumaticcraft.lib.Textures;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static me.desht.pneumaticcraft.common.util.PneumaticCraftUtils.xlate;

public class ProgWidgetItemFilter extends ProgWidget implements IVariableWidget {
    private ItemStack filter = ItemStack.EMPTY;
    public boolean useItemDurability;
    public boolean useNBT;
    public boolean useModSimilarity;
    public boolean matchBlock;
    private DroneAIManager aiManager;
    private String variable = "";

    public ProgWidgetItemFilter() {
        super(ModProgWidgets.ITEM_FILTER.get());
    }

    public static ProgWidgetItemFilter withFilter(ItemStack filter){
        ProgWidgetItemFilter widget = new ProgWidgetItemFilter();
        widget.filter = filter;
        return widget;
    }

    @Override
    public void addErrors(List<ITextComponent> curInfo, List<IProgWidget> widgets) {
        super.addErrors(curInfo, widgets);
        if (variable.equals("") && filter == null) {
            curInfo.add(xlate("pneumaticcraft.gui.progWidget.itemFilter.error.noFilter"));
        }
        if (matchBlock && !(filter.getItem() instanceof BlockItem) && variable.isEmpty()) {
            curInfo.add(xlate("pneumaticcraft.gui.progWidget.itemFilter.error.notBlock"));
        }
    }

    @Override
    public void addWarnings(List<ITextComponent> curInfo, List<IProgWidget> widgets) {
        super.addWarnings(curInfo, widgets);
        IProgWidget p = getParent();
        int n = 1;
        while (p != null) {
            // Item Assign widget only pays attention to the first connected item filter
            if (p instanceof ProgWidgetItemAssign && n > 1) {
                curInfo.add(xlate("pneumaticcraft.gui.progWidget.itemFilter.warning.ignoredItemAssign"));
                break;
            }
            n++;
            p = p.getParent();
        }
    }

    @Override
    public List<ITextComponent> getExtraStringInfo() {
        return Collections.singletonList(varAsTextComponent(variable));
    }

    @Nonnull
    public ItemStack getFilter() {
        return variable.isEmpty() ? filter : aiManager != null ? aiManager.getStack(variable) : ItemStack.EMPTY;
    }

    public ItemStack getRawFilter() {
        return filter;
    }

    public void setFilter(@Nonnull ItemStack filter) {
        this.filter = filter.copy();
    }

    @Override
    public void getTooltip(List<ITextComponent> curTooltip) {
        super.getTooltip(curTooltip);

        if (!variable.isEmpty()) {
            curTooltip.add(xlate("pneumaticcraft.gui.progWidget.coordinate.variable").append(": ").append(varAsTextComponent(variable)).withStyle(TextFormatting.AQUA));
        } else if (!filter.isEmpty()) {
            curTooltip.add(xlate("pneumaticcraft.gui.progWidget.itemFilter.filterLabel").withStyle(TextFormatting.AQUA)
                    .append(": ").append(filter.getHoverName()));
            if (filter.getItem() == ModItems.TAG_FILTER.get()) {
                curTooltip.addAll(ItemTagFilter.getConfiguredTagList(filter).stream()
                        .map(s -> Symbols.bullet().append(new StringTextComponent(s.toString()).withStyle(TextFormatting.YELLOW)))
                        .collect(Collectors.toList()));
            }
        }
        if (useModSimilarity) {
            curTooltip.add(xlate("pneumaticcraft.gui.progWidget.itemFilter.matchMod", ModNameCache.getModName(filter.getItem()))
                    .withStyle(TextFormatting.DARK_AQUA));
        } else if (matchBlock) {
            curTooltip.add(xlate("pneumaticcraft.gui.progWidget.itemFilter.matchBlock")
                    .withStyle(TextFormatting.DARK_AQUA));
        } else {
            curTooltip.add(xlate("pneumaticcraft.gui.progWidget.itemFilter." + (useItemDurability ? "useDurability" : "ignoreDurability"))
                    .withStyle(TextFormatting.DARK_AQUA));
            curTooltip.add(xlate("pneumaticcraft.gui.progWidget.itemFilter." + (useNBT ? "useNBT" : "ignoreNBT"))
                    .withStyle(TextFormatting.DARK_AQUA));
        }
    }

    @Override
    public boolean hasStepInput() {
        return false;
    }

    @Override
    public ProgWidgetType<?> returnType() {
        return ModProgWidgets.ITEM_FILTER.get();
    }

    @Override
    public List<ProgWidgetType<?>> getParameters() {
        return ImmutableList.of(ModProgWidgets.ITEM_FILTER.get());
    }

    @Override
    public ResourceLocation getTexture() {
        return Textures.PROG_WIDGET_ITEM_FILTER;
    }

    @Override
    public void writeToNBT(CompoundNBT tag) {
        super.writeToNBT(tag);
        if (!filter.isEmpty()) {
            filter.save(tag);
        }
        if (useItemDurability) tag.putBoolean("useMetadata", true);
        if (useNBT) tag.putBoolean("useNBT", true);
        if (useModSimilarity) tag.putBoolean("useModSimilarity", true);
        if (matchBlock) tag.putBoolean("matchBlock", true);
        if (!variable.isEmpty()) tag.putString("variable", variable);
    }

    @Override
    public void readFromNBT(CompoundNBT tag) {
        super.readFromNBT(tag);
        filter = ItemStack.of(tag);
        useItemDurability = filter.getMaxDamage() > 0 && tag.getBoolean("useMetadata");
        useNBT = tag.getBoolean("useNBT");
        useModSimilarity = tag.getBoolean("useModSimilarity");
        matchBlock = tag.getBoolean("matchBlock");
        variable = tag.getString("variable");
    }

    @Override
    public void writeToPacket(PacketBuffer buf) {
        super.writeToPacket(buf);
        buf.writeItem(filter);
        buf.writeBoolean(useItemDurability);
        buf.writeBoolean(useNBT);
        buf.writeBoolean(useModSimilarity);
        buf.writeBoolean(matchBlock);
        buf.writeUtf(variable);
    }

    @Override
    public void readFromPacket(PacketBuffer buf) {
        super.readFromPacket(buf);
        filter = buf.readItem();
        useItemDurability = buf.readBoolean();
        useNBT = buf.readBoolean();
        useModSimilarity = buf.readBoolean();
        matchBlock = buf.readBoolean();
        variable = buf.readUtf(GlobalVariableManager.MAX_VARIABLE_LEN);
    }

    public static boolean isItemValidForFilters(ItemStack item, List<ProgWidgetItemFilter> whitelist, List<ProgWidgetItemFilter> blacklist, BlockState blockState) {
        if (blacklist != null) {
            for (ProgWidgetItemFilter black : blacklist) {
                if (matchFilter(item, blockState, black)) return false;
            }
        }
        if (whitelist == null || whitelist.size() == 0) {
            return true;
        } else {
            for (ProgWidgetItemFilter white : whitelist) {
                if (matchFilter(item, blockState, white)) return true;
            }
            return false;
        }
    }

    private static boolean matchFilter(ItemStack stack, BlockState blockState, ProgWidgetItemFilter filter) {
        if (filter.matchBlock && blockState != null && filter.getFilter().getItem() instanceof BlockItem) {
            // match by block
            return blockState.getBlock() == ((BlockItem) filter.getFilter().getItem()).getBlock();
        } else {
            // match by item
            if (PneumaticCraftUtils.doesItemMatchFilter(filter.getFilter(), stack, filter.useItemDurability && blockState == null, filter.useNBT, filter.useModSimilarity)) {
                return blockState == null || !filter.useItemDurability;
            }
        }
        return false;
    }

    @Override
    public WidgetDifficulty getDifficulty() {
        return WidgetDifficulty.EASY;
    }

    @Override
    public DyeColor getColor() {
        return DyeColor.GRAY;
    }

    @Override
    public void setAIManager(DroneAIManager aiManager) {
        this.aiManager = aiManager;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public String getVariable() {
        return variable;
    }

    @Override
    public void addVariables(Set<String> variables) {
        variables.add(variable);
    }
}
