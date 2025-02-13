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

import me.desht.pneumaticcraft.client.gui.remote.RemoteLayout;
import me.desht.pneumaticcraft.client.gui.remote.actionwidget.ActionWidgetVariable;
import me.desht.pneumaticcraft.client.util.PointXY;
import me.desht.pneumaticcraft.common.inventory.ContainerRemote;
import me.desht.pneumaticcraft.common.tileentity.TileEntityBase;
import me.desht.pneumaticcraft.lib.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GuiRemote extends GuiPneumaticContainerBase<ContainerRemote,TileEntityBase> {

    RemoteLayout remoteLayout;
    protected final ItemStack remote;

    public GuiRemote(ContainerRemote container, PlayerInventory inv, ITextComponent displayString) {
        super(container, inv, displayString);

        imageWidth = 183;
        imageHeight = 202;
        this.remote = inv.player.getItemInHand(container.getHand());
    }

    /**
     * Client has received a PacketSetGlobalVariable message; update the remote GUI, if it's open.
     * @param varName variable that changed
     */
    public static void maybeHandleVariableChange(String varName) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof GuiRemote) {
            ((GuiRemote) screen).onGlobalVariableChange(varName);
        }
    }

    @Override
    public void init() {
        remoteLayout = null;

        super.init();

        if (remoteLayout == null) {
            remoteLayout = new RemoteLayout(remote, leftPos, topPos);
        }
        remoteLayout.getWidgets(!(this instanceof GuiRemoteEditor)).forEach(this::addButton);
    }

    @Override
    protected PointXY getInvTextOffset() {
        return null;
    }

    @Override
    protected boolean shouldAddProblemTab() {
        return false;
    }

    @Override
    protected ResourceLocation getGuiTexture() {
        return Textures.GUI_WIDGET_OPTIONS;
    }

    public void onGlobalVariableChange(String variable) {
        buttons.clear();
        children.clear();
        init();

        remoteLayout.getActionWidgets().stream()
                .filter(actionWidget -> actionWidget instanceof ActionWidgetVariable)
                .forEach(actionWidget -> ((ActionWidgetVariable<?>) actionWidget).onVariableChange());
    }

    @Override
    protected boolean shouldParseVariablesInTooltips() {
        return true;
    }
}
