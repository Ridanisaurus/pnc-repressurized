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

import me.desht.pneumaticcraft.api.crafting.AmadronTradeResource;
import me.desht.pneumaticcraft.api.item.IPositionProvider;
import me.desht.pneumaticcraft.client.gui.semiblock.GuiLogisticsLiquidFilter;
import me.desht.pneumaticcraft.client.gui.widget.*;
import me.desht.pneumaticcraft.client.util.ClientUtils;
import me.desht.pneumaticcraft.client.util.GuiUtils;
import me.desht.pneumaticcraft.client.util.PointXY;
import me.desht.pneumaticcraft.common.core.ModContainers;
import me.desht.pneumaticcraft.common.core.ModItems;
import me.desht.pneumaticcraft.common.inventory.ContainerAmadronAddTrade;
import me.desht.pneumaticcraft.common.item.ItemAmadronTablet;
import me.desht.pneumaticcraft.common.item.ItemGPSTool;
import me.desht.pneumaticcraft.common.network.NetworkHandler;
import me.desht.pneumaticcraft.common.network.PacketAmadronTradeAddCustom;
import me.desht.pneumaticcraft.common.network.PacketGuiButton;
import me.desht.pneumaticcraft.common.recipes.amadron.AmadronPlayerOffer;
import me.desht.pneumaticcraft.common.tileentity.TileEntityBase;
import me.desht.pneumaticcraft.common.util.GlobalPosHelper;
import me.desht.pneumaticcraft.common.util.PneumaticCraftUtils;
import me.desht.pneumaticcraft.lib.Textures;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.lwjgl.glfw.GLFW;

import java.util.List;

import static me.desht.pneumaticcraft.api.PneumaticRegistry.RL;
import static me.desht.pneumaticcraft.common.inventory.ContainerAmadronAddTrade.INPUT_SLOT;
import static me.desht.pneumaticcraft.common.inventory.ContainerAmadronAddTrade.OUTPUT_SLOT;
import static me.desht.pneumaticcraft.common.util.PneumaticCraftUtils.xlate;

public class GuiAmadronAddTrade extends GuiPneumaticContainerBase<ContainerAmadronAddTrade, TileEntityBase> {
    private GuiItemSearcher searchGui;
    private GuiInventorySearcher invSearchGui;
    private GuiInventorySearcher gpsSearchGui;
    private GuiLogisticsLiquidFilter fluidGui;
    private int settingSlot;

    private final WidgetFluidFilter[] fluidFilters = new WidgetFluidFilter[2];
    private final WidgetTextFieldNumber[] amountFields = new WidgetTextFieldNumber[2];
    private final BlockPos[] positions = new BlockPos[2];
    private Button addButton;
    private boolean openingSubGUI = false;

    public GuiAmadronAddTrade(ContainerAmadronAddTrade container, PlayerInventory inv, ITextComponent displayString) {
        super(container, inv, displayString);

        imageWidth = 183;
        imageHeight = 202;
    }

    @Override
    public void init() {
        super.init();

        if (searchGui != null) {
            setStack(settingSlot, searchGui.getSearchStack());
        } else if (invSearchGui != null) {
            setStack(settingSlot, invSearchGui.getSearchStack());
        } else if (fluidGui != null) {
            setFluid(settingSlot, fluidGui.getFilter());
        } else if (gpsSearchGui != null) {
            positions[settingSlot] = gpsSearchGui.getSearchStack().isEmpty() ?
                    null : ItemGPSTool.getGPSLocation(gpsSearchGui.getSearchStack());
        }
        openingSubGUI = false;
        searchGui = null;
        fluidGui = null;
        invSearchGui = null;
        gpsSearchGui = null;

        initSide(INPUT_SLOT);
        initSide(OUTPUT_SLOT);
        setFocused(amountFields[INPUT_SLOT]);
        addButton(addButton = new WidgetButtonExtended(leftPos + 50, topPos + 171, 85, 20, "Add Trade", b -> addTrade()));

        addJeiFilterInfoTab();
    }

    public void setStack(int slot, ItemStack stack) {
        fluidFilters[slot].setFluid(Fluids.EMPTY);
        menu.setStack(slot, stack);
    }

    public void setFluid(int slot, Fluid stack) {
        menu.setStack(slot, ItemStack.EMPTY);
        fluidFilters[slot].setFluid(stack);
    }

    private void initSide(int slot) {
        int xOffset = slot == INPUT_SLOT ? 0 : 89;

        String s = slot == INPUT_SLOT ? "selling" : "buying";
        addButton(new WidgetLabel(leftPos + 48 + xOffset, topPos + 7,
                xlate("pneumaticcraft.gui.amadron.addTrade." + s), 0xFFFFFFFF).setAlignment(WidgetLabel.Alignment.CENTRE));

        addButton(new WidgetButtonExtended(leftPos + 4 + xOffset, topPos + 20, 85, 20,
                xlate("pneumaticcraft.gui.misc.searchItem"), b -> openItemSearchGui(slot)));
        addButton(new WidgetButtonExtended(leftPos + 4 + xOffset, topPos + 42, 85, 20,
                xlate("pneumaticcraft.gui.misc.searchInventory"), b -> openInventorySearchGui(slot)));
        addButton(new WidgetButtonExtended(leftPos + 4 + xOffset, topPos + 64, 85, 20,
                xlate("pneumaticcraft.gui.misc.searchFluid"), b -> openFluidSearchGui(slot)));

        Fluid prev = fluidFilters[slot] != null ? fluidFilters[slot].getFluid() : Fluids.EMPTY;
        addButton(fluidFilters[slot] = new WidgetFluidFilter(leftPos + 37 + xOffset, topPos + 90, prev));

        String tip = slot == INPUT_SLOT ? "pneumaticcraft.gui.amadron.button.selectSellingBlock.tooltip" : "pneumaticcraft.gui.amadron.button.selectPaymentBlock.tooltip";
        addButton(new WidgetButtonExtended(leftPos + 10 + xOffset, topPos + 115, 20, 20, "", b -> openGPSGui(slot))
                .setRenderStacks(new ItemStack(ModItems.GPS_TOOL.get()))
                .setTooltipKey(tip)
        );

        int coarse = fluidFilters[slot].getFluid() == Fluids.EMPTY ? 10 : 1000;
        int max = fluidFilters[slot].getFluid() == Fluids.EMPTY ? 64 : Integer.MAX_VALUE;
        amountFields[slot] = new WidgetTextFieldNumber(font, leftPos + 22 + xOffset, topPos + 145, 40, font.lineHeight)
                .setValue(amountFields[slot] != null ? amountFields[slot].getIntValue() : 1).setRange(1, max).setAdjustments(1, coarse);
        addButton(amountFields[slot]);

        addButton(new WidgetLabel(leftPos + 65 + xOffset, topPos + 145,
                new StringTextComponent(fluidFilters[slot].getFluid() != Fluids.EMPTY ? "mB" : "") , 0xFFFFFFFF));
        GlobalPos p = getPosition(slot);
        if (p != null && GlobalPosHelper.isSameWorld(p, minecraft.level)) {
            BlockState state = minecraft.level.getBlockState(p.pos());
            ITextComponent name = new ItemStack(state.getBlock().asItem()).getHoverName();
            addButton(new WidgetLabel(leftPos + 32 + xOffset, topPos + 118,
                    name, 0xFFFFFFFF)).setScale(0.5f);
            addButton(new WidgetLabel(leftPos + 32 + xOffset, topPos + 124,
                    new StringTextComponent(" @ " + posToString(p.pos())), 0xFFFFFFFF)).setScale(0.5f);
        }
        if (positions[slot] == null) {
            addButton(new WidgetLabel(leftPos + 32 + xOffset, topPos + 130, new StringTextComponent("[Default]"), 0xFFC0C0C0)).setScale(0.5f);
        }
    }

    private String posToString(BlockPos pos) {
        return pos == null ? "[Default]" : PneumaticCraftUtils.posToString(pos);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void removed() {
        if (!openingSubGUI) {
            NetworkHandler.sendToServer(new PacketGuiButton("showAmadron"));
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            removed();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_TAB) {
            WidgetTextFieldNumber amountIn = amountFields[INPUT_SLOT];
            WidgetTextFieldNumber amountOut = amountFields[OUTPUT_SLOT];
            if (amountIn.isFocused()) {
                amountIn.moveCursorTo(amountIn.getCursorPosition());
                amountIn.setFocus(false);
                setFocused(amountOut);
                amountOut.moveCursorTo(0);
                amountOut.setHighlightPos(amountOut.getValue().length());
            } else if (amountOut.isFocused()) {
                amountOut.moveCursorTo(amountOut.getCursorPosition());
                amountOut.setFocus(false);
                setFocused(amountIn);
                amountIn.moveCursorTo(0);
                amountIn.setHighlightPos(amountIn.getValue().length());
            }
        }

        if (getFocused() instanceof WidgetTextField) {
            WidgetTextField tf = (WidgetTextField) getFocused();
            return tf.keyPressed(keyCode, scanCode, modifiers) || tf.canConsumeInput() || super.keyPressed(keyCode, scanCode, modifiers);
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    protected int getBackgroundTint() {
        return 0xFF068E2C;
    }

    @Override
    protected ResourceLocation getGuiTexture() {
        return Textures.GUI_AMADRON_ADD_TRADE;
    }

    private void openItemSearchGui(int slot) {
        openingSubGUI = true;
        ClientUtils.openContainerGui(ModContainers.ITEM_SEARCHER.get(),
                new TranslationTextComponent("pneumaticcraft.gui.amadron.addTrade.itemSearch"));
        if (minecraft.screen instanceof GuiItemSearcher) {
            settingSlot = slot;
            searchGui = (GuiItemSearcher) minecraft.screen;
            searchGui.setSearchStack(menu.getStack(slot));
        }
    }

    private void openInventorySearchGui(int slot) {
        openingSubGUI = true;
        ClientUtils.openContainerGui(ModContainers.INVENTORY_SEARCHER.get(),
                new TranslationTextComponent("pneumaticcraft.gui.amadron.addTrade.invSearch"));
        if (minecraft.screen instanceof GuiInventorySearcher) {
            settingSlot = slot;
            invSearchGui = (GuiInventorySearcher) minecraft.screen;
            invSearchGui.setSearchStack(menu.getStack(slot));
        }
    }

    private void openFluidSearchGui(int slot) {
        openingSubGUI = true;
        settingSlot = slot;
        fluidGui = new GuiLogisticsLiquidFilter(this);
        fluidGui.setFilter(fluidFilters[slot].getFluid());
        minecraft.setScreen(fluidGui);
    }

    private void openGPSGui(int slot) {
        openingSubGUI = true;
        ClientUtils.openContainerGui(ModContainers.INVENTORY_SEARCHER.get(),
                new TranslationTextComponent("pneumaticcraft.gui.amadron.addTrade.gpsSearch"));
        if (minecraft.screen instanceof GuiInventorySearcher) {
            gpsSearchGui = (GuiInventorySearcher) minecraft.screen;
            gpsSearchGui.setStackPredicate(itemStack -> itemStack.getItem() instanceof IPositionProvider);
            settingSlot = slot;
            ItemStack gps = new ItemStack(ModItems.GPS_TOOL.get());
            GlobalPos gPos = getPosition(slot);
            if (gPos != null) ItemGPSTool.setGPSLocation(gps, gPos.pos());
            gpsSearchGui.setSearchStack(ItemGPSTool.getGPSLocation(gps) != null ? gps : ItemStack.EMPTY);
        }
    }

    private void addTrade() {
        AmadronTradeResource[] resources = new AmadronTradeResource[2];
        for (int slot = 0; slot < resources.length; slot++) {
            if (!menu.getStack(slot).isEmpty()) {
                resources[slot] = AmadronTradeResource.of(ItemHandlerHelper.copyStackWithSize(menu.getStack(slot), amountFields[slot].getIntValue()));
            } else {
                resources[slot] = AmadronTradeResource.of(new FluidStack(fluidFilters[slot].getFluid(), amountFields[slot].getIntValue()));
            }
        }
        String id = minecraft.player.getName().getContents().toLowerCase() + "_" + (System.currentTimeMillis() / 1000);
        AmadronPlayerOffer trade = new AmadronPlayerOffer(RL(id), resources[OUTPUT_SLOT], resources[INPUT_SLOT], minecraft.player)
                .setProvidingPosition(getPosition(INPUT_SLOT))
                .setReturningPosition(getPosition(OUTPUT_SLOT));
        NetworkHandler.sendToServer(new PacketAmadronTradeAddCustom(trade));
    }

    private GlobalPos getPosition(int slot) {
        if (positions[slot] != null) {
            return GlobalPosHelper.makeGlobalPos(inventory.player.level, positions[slot]);
        }
        if (!menu.getStack(slot).isEmpty()) {
            return ItemAmadronTablet.getItemProvidingLocation(inventory.player.getMainHandItem());
        } else if (fluidFilters[slot].getFluid() != Fluids.EMPTY) {
            return ItemAmadronTablet.getFluidProvidingLocation(inventory.player.getMainHandItem());
        } else {
            return null;
        }
    }

    @Override
    protected PointXY getInvTextOffset() {
        return null;
    }

    @Override
    public void tick() {
        super.tick();

        GlobalPos inPos = getPosition(INPUT_SLOT);
        GlobalPos outPos = getPosition(OUTPUT_SLOT);

        addButton.active = amountFields[INPUT_SLOT].getIntValue() > 0 && amountFields[OUTPUT_SLOT].getIntValue() > 0
                && (fluidFilters[INPUT_SLOT].getFluid() != Fluids.EMPTY || !menu.getInputStack().isEmpty())
                && (fluidFilters[OUTPUT_SLOT].getFluid() != Fluids.EMPTY || !menu.getOutputStack().isEmpty())
                && inPos != null && outPos != null;
    }

    @Override
    protected void addProblems(List<ITextComponent> curInfo) {
        if (getPosition(INPUT_SLOT) == null || getPosition(OUTPUT_SLOT) == null) {
            curInfo.addAll(GuiUtils.xlateAndSplit("pneumaticcraft.gui.amadron.addTrade.problems.noSellingOrPayingBlock"));
        }
        super.addProblems(curInfo);
    }
}
