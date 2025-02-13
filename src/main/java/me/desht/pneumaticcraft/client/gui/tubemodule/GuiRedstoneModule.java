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

package me.desht.pneumaticcraft.client.gui.tubemodule;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import me.desht.pneumaticcraft.api.misc.Symbols;
import me.desht.pneumaticcraft.client.gui.widget.*;
import me.desht.pneumaticcraft.client.util.GuiUtils;
import me.desht.pneumaticcraft.common.block.tubes.ModuleRedstone;
import me.desht.pneumaticcraft.common.block.tubes.ModuleRedstone.EnumRedstoneDirection;
import me.desht.pneumaticcraft.common.block.tubes.ModuleRedstone.Operation;
import me.desht.pneumaticcraft.common.core.ModSounds;
import me.desht.pneumaticcraft.common.network.NetworkHandler;
import me.desht.pneumaticcraft.common.network.PacketSyncRedstoneModuleToServer;
import me.desht.pneumaticcraft.lib.Textures;
import net.minecraft.item.DyeColor;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

import static me.desht.pneumaticcraft.common.util.PneumaticCraftUtils.dyeColorDesc;
import static me.desht.pneumaticcraft.common.util.PneumaticCraftUtils.xlate;

public class GuiRedstoneModule extends GuiTubeModule<ModuleRedstone> {
    private WidgetComboBox comboBox;
    private WidgetLabel constLabel;
    private WidgetTextFieldNumber constTextField;
    private WidgetLabel otherColorLabel;
    private WidgetColorSelector otherColorButton;
    private int ourColor;
    private int otherColor;
    private WidgetCheckBox invertCheckBox;
    private WidgetCheckBox comparatorInputCheckBox;
    private boolean upgraded;
    private boolean output;
    private final List<IReorderingProcessor> lowerText = new ArrayList<>();

    public GuiRedstoneModule(ModuleRedstone module) {
        super(module);

        ySize = module.getRedstoneDirection() == EnumRedstoneDirection.OUTPUT ? 202 : 57;
    }

    @Override
    protected ResourceLocation getTexture() {
        return output ? Textures.GUI_WIDGET_OPTIONS : Textures.GUI_MODULE_SIMPLE;
    }

    @Override
    public void init() {
        super.init();

        upgraded = module.isUpgraded();
        output = module.getRedstoneDirection() == EnumRedstoneDirection.OUTPUT;
        ourColor = module.getColorChannel();
        otherColor = module.getOtherColor();

        addButton(new WidgetButtonExtended(guiLeft + xSize - 22, guiTop + 2, 18, 12,
                getDirText(module), b -> toggleRedstoneDirection())
                .setTooltipText(ImmutableList.of(
                        xlate(module.getRedstoneDirection().getTranslationKey()),
                        xlate("pneumaticcraft.gui.redstoneModule.clickToToggle").withStyle(TextFormatting.GRAY)
                ))
        );

        addButton(new WidgetLabel(guiLeft + xSize / 2, guiTop + 5, getTitle()).setAlignment(WidgetLabel.Alignment.CENTRE));

        WidgetLabel ourColorLabel;
        addButton(ourColorLabel = new WidgetLabel(guiLeft + 10, guiTop + 25, xlate("pneumaticcraft.gui.tubeModule.channel")));

        WidgetLabel opLabel;
        addButton(opLabel = new WidgetLabel(guiLeft + 10, guiTop + 45, xlate("pneumaticcraft.gui.redstoneModule.operation")));
        opLabel.visible = output;

        otherColorLabel = new WidgetLabel(guiLeft + 10, guiTop + 65, xlate("pneumaticcraft.gui.tubeModule.otherChannel"));
        otherColorLabel.visible = output;
        addButton(otherColorLabel);

        constLabel = new WidgetLabel(guiLeft + 15, guiTop + 65, xlate("pneumaticcraft.gui.redstoneModule.constant"));
        addButton(constLabel);
        constLabel.visible = output;

        int w = 0;
        for (WidgetLabel label : ImmutableList.of(ourColorLabel, otherColorLabel, opLabel, constLabel)) {
            w = Math.max(label.getWidth(), w);
        }
        int xBase = guiLeft + w + 15;

        addButton(new WidgetColorSelector(xBase, guiTop + 20, b -> ourColor = b.getColor().getId())
                .withInitialColor(DyeColor.byId(ourColor)));

        if (!output) {
            comparatorInputCheckBox = new WidgetCheckBox(guiLeft + 10, guiTop + 40, 0xFF404040, xlate("pneumaticcraft.gui.redstoneModule.comparatorInput"));
            comparatorInputCheckBox.setChecked(module.isComparatorInput());
            comparatorInputCheckBox.setTooltipKey("pneumaticcraft.gui.redstoneModule.comparatorInput.tooltip");
            comparatorInputCheckBox.visible = !output && upgraded;
            addButton(comparatorInputCheckBox);
        } else {
            comboBox = new WidgetComboBox(font, xBase, guiTop + 43, xSize - xBase + guiLeft - 10, 12)
                    .initFromEnum(module.getOperation());
            comboBox.active = upgraded;
            addButton(comboBox);

            otherColorButton = new WidgetColorSelector(xBase, guiTop + 60, b -> otherColor = b.getColor().getId())
                    .withInitialColor(DyeColor.byId(otherColor));
            otherColorButton.active = upgraded;
            addButton(otherColorButton);

            constTextField = new WidgetTextFieldNumber(font, xBase, guiTop + 63, 30, 12);
            constTextField.minValue = 0;
            constTextField.setDecimals(0);
            constTextField.setValue(module.getConstantVal());
            constTextField.active = upgraded;
            addButton(constTextField);

            invertCheckBox = new WidgetCheckBox(guiLeft + 10, guiTop + 85, 0xFF404040, xlate("pneumaticcraft.gui.redstoneModule.invert")) {
                @Override
                public boolean mouseClicked(double mouseX, double mouseY, int button) {
                    if (comboBox.isFocused()) return true;  // it hangs over the button
                    return super.mouseClicked(mouseX, mouseY, button);
                }
            };
            invertCheckBox.setChecked(module.isInverted());
            invertCheckBox.setTooltipKey("pneumaticcraft.gui.redstoneModule.invert.tooltip");
            addButton(invertCheckBox);

            updateWidgetVisibility();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (output) {
            if (upgraded) {
                updateWidgetVisibility();
            }
            updateLowerText();
        }
    }

    private void updateWidgetVisibility() {
        Operation op = getSelectedOp();
        constLabel.visible = op.useConstant();
        constTextField.setVisible(op.useConstant());
        constTextField.setRange(op.getConstMin(), op.getConstMax());
        otherColorLabel.visible = op.useOtherColor();
        otherColorButton.visible = op.useOtherColor();
        otherColorButton.setVisible(op.useOtherColor());
    }

    private void updateLowerText() {
        lowerText.clear();

        Operation op = getSelectedOp();
        String key = op.getTranslationKey() + ".tooltip";
        List<ITextComponent> l = new ArrayList<>();
        if (op.useConstant()) {
            l.add(xlate(key, dyeColorDesc(ourColor), constTextField.getValue()));
        } else if (op.useOtherColor()) {
            l.add(xlate(key, dyeColorDesc(ourColor), dyeColorDesc(otherColor)));
        } else {
            l.add(xlate(key, dyeColorDesc(ourColor)));
        }
        if (!upgraded) {
            l.add(xlate("pneumaticcraft.gui.redstoneModule.addAdvancedPCB").withStyle(TextFormatting.DARK_BLUE));
        }
        lowerText.addAll(GuiUtils.wrapTextComponentList(l, xSize - 20, font));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        int yBase = guiTop + ySize - lowerText.size() * font.lineHeight - 10;
        for (int i = 0; i < lowerText.size(); i++) {
            font.draw(matrixStack, lowerText.get(i), guiLeft + 10, yBase + i * font.lineHeight, 0xFF404040);
        }
    }

    private Operation getSelectedOp() {
        return Operation.values()[comboBox.getSelectedElementIndex()];
    }

    @Override
    public void removed() {
        super.removed();

        module.setColorChannel(ourColor);
        if (output) {
            module.setInverted(invertCheckBox.checked);
            module.setOperation(getSelectedOp(), otherColor, constTextField.getIntValue());
        } else {
            module.setComparatorInput(comparatorInputCheckBox.checked);
        }
        NetworkHandler.sendToServer(new PacketSyncRedstoneModuleToServer(module));
    }

    private void toggleRedstoneDirection() {
        module.setRedstoneDirection(module.getRedstoneDirection().toggle());

        // close and re-open... will call onClose() to sync the settings
        removed();
        minecraft.setScreen(new GuiRedstoneModule(module));
        minecraft.player.playSound(ModSounds.INTERFACE_DOOR.get(), 0.7f, 2f);
    }

    private String getDirText(ModuleRedstone module) {
        return module.getRedstoneDirection() == EnumRedstoneDirection.INPUT ?
                TextFormatting.DARK_RED + Symbols.TRIANGLE_LEFT :
                TextFormatting.RED + Symbols.TRIANGLE_RIGHT;
    }
}
