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

package me.desht.pneumaticcraft.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.desht.pneumaticcraft.api.crafting.TemperatureRange;
import me.desht.pneumaticcraft.client.util.GuiUtils;
import me.desht.pneumaticcraft.common.heat.HeatUtil;
import me.desht.pneumaticcraft.lib.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static me.desht.pneumaticcraft.api.crafting.TemperatureRange.TemperatureScale.CELSIUS;
import static me.desht.pneumaticcraft.common.util.PneumaticCraftUtils.xlate;

public class WidgetTemperature extends Widget implements ITooltipProvider {
    private int temperature;
    private int tickInterval;
    private TemperatureRange totalRange;
    private TemperatureRange operatingRange;
    private boolean drawText = true;
    private boolean showOperatingRange = true;

    public WidgetTemperature(int xIn, int yIn, TemperatureRange totalRange, int initialTemp, int tickInterval) {
        super(xIn, yIn, 13, 50, StringTextComponent.EMPTY);
        this.totalRange = totalRange;
        this.temperature = initialTemp;
        this.tickInterval = tickInterval;
        this.operatingRange = null;
    }

    public void setTotalRange(@Nonnull TemperatureRange totalRange) {
        this.totalRange = totalRange;
    }

    public TemperatureRange getTotalRange() {
        return totalRange;
    }

    public WidgetTemperature setOperatingRange(@Nullable TemperatureRange operatingRange) {
        this.operatingRange = operatingRange;
        return this;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public void setTickInterval(int tickInterval) {
        this.tickInterval = tickInterval;
    }

    public <T extends WidgetTemperature> T setDrawText(boolean drawText) {
        this.drawText = drawText;
        return (T) this;
    }

    public WidgetTemperature setShowOperatingRange(boolean showOperatingRange) {
        this.showOperatingRange = showOperatingRange;
        return this;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            RenderSystem.disableLighting();
            Minecraft.getInstance().getTextureManager().bind(Textures.WIDGET_TEMPERATURE);
            RenderSystem.color4f(1, 1, 1, 1);

            // the background frame
            blit(matrixStack, x + 6, y, 6, 0, 7, 50, 18, 50);

            // the coloured temperature bar
            int barLength = getYPos(temperature);
            blit(matrixStack, x + 7, y - 1 + height - barLength, 13, height - barLength - 2, 5, barLength, 18, 50);

            // ticks
            drawTicks(matrixStack);
            if (drawText) {
                GuiUtils.drawScaledText(matrixStack, Minecraft.getInstance().font, CELSIUS.symbol(), x + 7, y + height + 1, 0xFF404040, 0.5f);
            }

            // operating temp markers, if necessary
            drawOperatingTempMarkers(matrixStack);
        }
    }

    public void drawTicks(MatrixStack matrixStack) {
        FontRenderer font = Minecraft.getInstance().font;
        int tickTempC = findNearestCelsius(totalRange.getMin() - 273, tickInterval);
        int n = 0;
        while (tickTempC <= totalRange.getMax() - 273) {
            // draw...
            int yOffset = getYPos(tickTempC + 273);
            hLine(matrixStack, x + 4, x + 6, y - 1 + height - yOffset, 0xA0404040);
            if (yOffset != 0 && yOffset != height - 1) {
                hLine(matrixStack, x + 6, x + 8, y - 1 + height - yOffset, 0x80C0C0C0);
            }
            if (drawText && n % 2 == 0) { // && (tickInterval > 10 || n > 0)) {
                String s = Integer.toString(tickTempC);
                GuiUtils.drawScaledText(matrixStack, font, s, x + 4 - font.width(s) / 2, y - 2 + height - yOffset, 0xFF404040, 0.5f);
            }
            n++;
            tickTempC += tickInterval;
        }
    }

    public void drawOperatingTempMarkers(MatrixStack matrixStack) {
        if (operatingRange != null) {
            if (totalRange.inRange(operatingRange.getMax())) {
                int yOffset = getYPos(operatingRange.getMax());
                hLine(matrixStack, x + 7, x + 11, y + 1 + height - yOffset, 0xFFE0E040);
                hLine(matrixStack, x + 9, x + 9,  y     + height - yOffset, 0x80E0E040);
                hLine(matrixStack, x + 8, x + 10, y - 1 + height - yOffset, 0x80E0E040);
                hLine(matrixStack, x + 7, x + 11, y - 2 + height - yOffset, 0x80E0E040);
            }
            if (totalRange.inRange(operatingRange.getMin())) {
                int yOffset = getYPos(operatingRange.getMin());
                hLine(matrixStack, x + 7, x + 11, y - 1 + height - yOffset, 0xFFE0E040);
                hLine(matrixStack, x + 9, x + 9,  y     + height - yOffset, 0x80E0E040);
                hLine(matrixStack, x + 8, x + 10, y + 1 + height - yOffset, 0x80E0E040);
                hLine(matrixStack, x + 7, x + 11, y + 2 + height - yOffset, 0x80E0E040);
            }
        }
    }

    private int getYPos(int temp) {
        int h = height - 1;
        return MathHelper.clamp((temp - totalRange.getMin()) * h / (totalRange.getMax() - totalRange.getMin()), 0, h + 4);
    }

    private static int findNearestCelsius(int temp, int interval) {
        int q = temp % interval;
        if (q == 0) return temp;
        int n = temp - q;
        return Math.max(-273, n - interval);
    }

    @Override
    public void addTooltip(double mouseX, double mouseY, List<ITextComponent> curTip, boolean shift) {
        curTip.add(HeatUtil.formatHeatString(temperature));
        if (operatingRange != null && showOperatingRange) {
            TextFormatting tf = operatingRange.inRange(temperature) ? TextFormatting.GREEN : TextFormatting.GOLD;
            curTip.add(xlate("pneumaticcraft.gui.misc.requiredTemperatureString", operatingRange.asString(CELSIUS)).withStyle(tf));
        }
    }

    public void autoScaleForTemperature() {
        if (temperature < 173) {
            setTotalRange(TemperatureRange.of(0, 275));
            setTickInterval(25);
        } else if (temperature < 273) {
            setTotalRange(TemperatureRange.of(123, 373));
            setTickInterval(25);
        } else if (temperature < 373) {
            setTotalRange(TemperatureRange.of(273, 373));
            setTickInterval(25);
        } else if (temperature < 473) {
            setTotalRange(TemperatureRange.of(273, 473));
            setTickInterval(25);
        } else if (temperature < 773) {
            setTotalRange(TemperatureRange.of(273, 773));
            setTickInterval(50);
        } else if (temperature < 1273) {
            setTotalRange(TemperatureRange.of(273, 1273));
            setTickInterval(100);
        } else {
            setTotalRange(TemperatureRange.of(273, 2273));
            setTickInterval(200);
        }
    }

    public static int roundDownK(int tempK, int interval) {
        int tempC = tempK - 273;
        int rem = tempC % interval;
        return tempK - rem;
    }

    public static int roundUpK(int tempK, int interval) {
        return roundDownK(tempK, interval) + interval;
    }

    public static WidgetTemperature fromOperatingRange(int x, int y, TemperatureRange range) {
        int interval = calcInterval(range.getMax() - range.getMin());
        TemperatureRange totalRange;
        if (range.hasMin() && range.hasMax()) {
            totalRange = TemperatureRange.of(roundDownK(range.getMin(), interval), roundUpK(range.getMax(), interval));
        } else if (range.hasMin()) {
            int minK = roundDownK(range.getMin(), interval);
            totalRange = TemperatureRange.of(minK, minK + interval * 2);
        } else if (range.hasMax()) {
            totalRange = TemperatureRange.of(0, roundUpK(range.getMax(), interval));
        } else {
            totalRange = TemperatureRange.of(0, 2273);
        }
        interval = calcInterval(totalRange.getMax() - totalRange.getMin()) * 2;
        return new WidgetTemperature(x, y, totalRange, range.getMin(), interval).setOperatingRange(range);
    }

    public static int calcInterval(int r) {
        int r1 = r / 10;
        if (r1 >= 200) {
            return 200;
        } else if (r1 >= 100) {
            return 100;
        } else if (r1 >= 50) {
            return 50;
        } else if (r1 >= 15) {
            return 25;
        } else if (r1 >= 5) {
            return 10;
        } else {
            return 5;
        }
    }
}
