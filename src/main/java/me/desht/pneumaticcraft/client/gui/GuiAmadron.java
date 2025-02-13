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

import com.google.common.collect.ImmutableList;
import me.desht.pneumaticcraft.api.crafting.recipe.AmadronRecipe;
import me.desht.pneumaticcraft.api.misc.Symbols;
import me.desht.pneumaticcraft.client.gui.widget.*;
import me.desht.pneumaticcraft.client.util.ClientUtils;
import me.desht.pneumaticcraft.client.util.GuiUtils;
import me.desht.pneumaticcraft.client.util.PointXY;
import me.desht.pneumaticcraft.common.amadron.AmadronOfferManager;
import me.desht.pneumaticcraft.common.inventory.ContainerAmadron;
import me.desht.pneumaticcraft.common.inventory.ContainerAmadron.EnumProblemState;
import me.desht.pneumaticcraft.common.network.NetworkHandler;
import me.desht.pneumaticcraft.common.network.PacketAmadronOrderUpdate;
import me.desht.pneumaticcraft.common.tileentity.TileEntityBase;
import me.desht.pneumaticcraft.lib.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static me.desht.pneumaticcraft.common.util.PneumaticCraftUtils.xlate;

public class GuiAmadron extends GuiPneumaticContainerBase<ContainerAmadron,TileEntityBase> {
    private static final int ROWS = 4;
    public static final int OFFERS_PER_PAGE = ROWS * 2;

    private WidgetTextField searchBar;
    private WidgetVerticalScrollbar scrollbar;
    private WidgetLabel pageLabel;
    private int page = 0;
    private final List<WidgetAmadronOfferAdjustable> offerWidgets = new ArrayList<>();
    private boolean needsRefreshing;
    private boolean hadProblem = false;
    private WidgetButtonExtended orderButton;
    private WidgetButtonExtended addTradeButton;
    private WidgetAnimatedStat customTradesTab;
    private boolean needTooltipUpdate = true;
    private int problemTimer = 0;

    public GuiAmadron(ContainerAmadron container, PlayerInventory inv, @SuppressWarnings("unused") ITextComponent displayString) {
        super(container, inv, new StringTextComponent(""));
        imageWidth = 176;
        imageHeight = 202;
    }

    @Override
    public void init() {
        super.init();

        ITextComponent amadron = xlate("pneumaticcraft.gui.amadron.title");
        addLabel(amadron, leftPos + imageWidth / 2 - font.width(amadron) / 2, topPos + 5, 0xFFFFFF).setDropShadow(true);
        addLabel(xlate("pneumaticcraft.gui.search"), leftPos + 76 - font.width(I18n.get("pneumaticcraft.gui.search")), topPos + 41, 0xFFFFFF);

        addInfoTab(xlate("gui.tooltip.item.pneumaticcraft.amadron_tablet"));
        addAnimatedStat(xlate("pneumaticcraft.gui.tab.info.ghostSlotInteraction.title"), Textures.GUI_MOUSE_LOCATION, 0xFF00AAFF, true)
                .setText(GuiUtils.xlateAndSplit("pneumaticcraft.gui.tab.info.ghostSlotInteraction"));
        addAnimatedStat(xlate("pneumaticcraft.gui.tab.amadron.disclaimer.title"), new ItemStack(Items.WRITABLE_BOOK), 0xFF0000FF, true)
                .setText(xlate("pneumaticcraft.gui.tab.amadron.disclaimer"));
        customTradesTab = addAnimatedStat(xlate("pneumaticcraft.gui.tab.amadron.customTrades"), new ItemStack(Items.DIAMOND), 0xFFD07000, false);
        customTradesTab.setMinimumExpandedDimensions(80, 50);
        searchBar = new WidgetTextField(font, leftPos + 79, topPos + 40, 73, font.lineHeight);
        searchBar.setResponder(s -> sendDelayed(8));
        addButton(searchBar);
        setFocused(searchBar);

        addButton(scrollbar = new WidgetVerticalScrollbar(leftPos + 156, topPos + 54, 142).setStates(1).setListening(true));

        orderButton = new WidgetButtonExtended(leftPos + 52, topPos + 16, 72, 20, xlate("pneumaticcraft.gui.amadron.button.order"))
                .withTag("order");
        addButton(orderButton);
        updateOrderButtonTooltip();

        addTradeButton = new WidgetButtonExtended(16, 26, 20, 20)
                .withTag("addPlayerTrade")
                .setRenderStacks(new ItemStack(Items.EMERALD))
                .setTooltipText(ImmutableList.of(
                        xlate("pneumaticcraft.gui.amadron.button.addTrade"),
                        xlate("pneumaticcraft.gui.amadron.button.addTrade.tooltip")
                ));
        customTradesTab.addSubWidget(addTradeButton);

        addButton(pageLabel = new WidgetLabel(leftPos + 158, topPos + 49, new StringTextComponent("")));
        pageLabel.setScale(0.5f);
        pageLabel.setColor(0xFFE0E0E0);

        needsRefreshing = true;
    }

    public static void basketUpdated() {
        if (Minecraft.getInstance().screen instanceof GuiAmadron) {
            ((GuiAmadron) Minecraft.getInstance().screen).needTooltipUpdate = true;
        }
    }

    @Override
    protected void doDelayedAction() {
        needsRefreshing = true;
        scrollbar.setCurrentState(0);
    }

    @Override
    public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
        return scrollbar.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
    }

    @Override
    protected int getBackgroundTint() {
        return 0x068e2c;
    }

    @Override
    protected ResourceLocation getGuiTexture() {
        return Textures.GUI_AMADRON;
    }

    @Override
    public void tick() {
        super.tick();

        if (needsRefreshing || page != scrollbar.getState()) {
            setPage(scrollbar.getState());
        }
        for (WidgetAmadronOfferAdjustable offerWidget : offerWidgets) {
            offerWidget.setAffordable(menu.affordableOffers[offerWidget.index]);
            offerWidget.setShoppingAmount(menu.getShoppingBasketUnits(offerWidget.getOffer().getId()));
        }
        if (!hadProblem && menu.problemState != EnumProblemState.NO_PROBLEMS) {
            problemTab.openStat();
        }
        hadProblem = menu.problemState != EnumProblemState.NO_PROBLEMS;
        addTradeButton.active = menu.currentPlayerOffers < menu.maxPlayerOffers;
        ITextComponent text = xlate("pneumaticcraft.gui.amadron.button.addTrade.tooltip.offerCount",
                menu.currentPlayerOffers,
                menu.maxPlayerOffers == Integer.MAX_VALUE ? Symbols.INFINITY : menu.maxPlayerOffers);
        customTradesTab.setText(text);

        orderButton.active = !menu.isBasketEmpty() && menu.problemState == EnumProblemState.NO_PROBLEMS;

        if (needTooltipUpdate) {
            updateOrderButtonTooltip();
            needTooltipUpdate = false;
        }

        // since amadron problems cap the order, the order amount stays valid
        // so the problem report should time out after a few seconds
        if (problemTimer == 0 && menu.problemState != EnumProblemState.NO_PROBLEMS) {
            problemTimer = 70;
        } else if (problemTimer > 0) {
            if (--problemTimer <= 0) menu.problemState = EnumProblemState.NO_PROBLEMS;
        }
    }

    private void updateOrderButtonTooltip() {
        ImmutableList.Builder<ITextComponent> builder = ImmutableList.builder();
        builder.add(xlate("pneumaticcraft.gui.amadron.button.order.tooltip"));
        if (!menu.isBasketEmpty()) {
            builder.add(StringTextComponent.EMPTY);
            builder.add(xlate("pneumaticcraft.gui.amadron.basket").withStyle(TextFormatting.AQUA, TextFormatting.UNDERLINE));
            for (AmadronRecipe offer : AmadronOfferManager.getInstance().getActiveOffers()) {
                int nOrders = menu.getShoppingBasketUnits(offer.getId());
                if (nOrders > 0) {
                    String in = (offer.getInput().getAmount() * nOrders) + " x " + offer.getInput().getName();
                    String out = (offer.getOutput().getAmount() * nOrders) + " x " + offer.getOutput().getName();
                    builder.add(new StringTextComponent(Symbols.BULLET + " " + TextFormatting.YELLOW + out));
                    builder.add(new StringTextComponent(TextFormatting.GOLD + "   for " + TextFormatting.YELLOW + in));
                }
            }
        }
        orderButton.setTooltipText(builder.build());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            minecraft.player.closeContainer();
        }

        return searchBar.keyPressed(keyCode, scanCode, modifiers)
                || searchBar.canConsumeInput()
                || super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void setPage(int page) {
        this.page = page;

        updateVisibleOffers();

        int nPages = ((menu.activeOffers.size() - 1) / OFFERS_PER_PAGE) + 1;
        pageLabel.setMessage(new StringTextComponent((page + 1) + "/" + nPages));
    }

    private void updateVisibleOffers() {
        needsRefreshing = false;
        List<AmadronRecipe> visibleOffers = new ArrayList<>();
        int skippedOffers = 0;
        int applicableOffers = 0;

        for (int i = 0; i < menu.activeOffers.size(); i++) {
            AmadronRecipe offer = menu.activeOffers.get(i);
            if (offer.passesQuery(searchBar.getValue())) {
                applicableOffers++;
                if (skippedOffers < page * OFFERS_PER_PAGE) {
                    skippedOffers++;
                } else if (visibleOffers.size() < OFFERS_PER_PAGE) {
                    visibleOffers.add(offer);
                }
            }
        }

        scrollbar.setStates(Math.max(1, (applicableOffers + OFFERS_PER_PAGE - 1) / OFFERS_PER_PAGE - 1));

        buttons.removeAll(offerWidgets);
        children.removeAll(offerWidgets);
        offerWidgets.clear();
        for (int i = 0; i < visibleOffers.size(); i++) {
            AmadronRecipe offer = visibleOffers.get(i);
            int idx = menu.activeOffers.indexOf(offer);
            if (idx >= 0) {  // should always be the case; sanity check
                WidgetAmadronOfferAdjustable widget = new WidgetAmadronOfferAdjustable(leftPos + 6 + 73 * (i % 2), topPos + 55 + 35 * (i / 2), offer, idx);
                addButton(widget);
                offerWidgets.add(widget);
            }
        }
    }

    @Override
    protected PointXY getInvTextOffset() {
        return null;
    }

    @Override
    protected void addProblems(List<ITextComponent> curInfo) {
        super.addProblems(curInfo);
        if (menu.problemState != EnumProblemState.NO_PROBLEMS) {
            curInfo.addAll(GuiUtils.xlateAndSplit(menu.problemState.getTranslationKey()));
        }
    }

    @Override
    public void onGuiUpdate() {
        needTooltipUpdate = true;
    }

    static class WidgetAmadronOfferAdjustable extends WidgetAmadronOffer {
        // index into the current active offers list
        private final int index;

        WidgetAmadronOfferAdjustable(int x, int y, AmadronRecipe offer, int index) {
            super(x, y, offer);
            this.index = index;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
            if (super.mouseClicked(mouseX, mouseY, mouseButton)) return true;
            if (clicked(mouseX, mouseY) && getOffer().isUsableByPlayer(ClientUtils.getClientPlayer())) {
                NetworkHandler.sendToServer(new PacketAmadronOrderUpdate(getOffer().getId(), mouseButton, Screen.hasShiftDown()));
                return true;
            } else {
                return false;
            }
        }
    }
}
