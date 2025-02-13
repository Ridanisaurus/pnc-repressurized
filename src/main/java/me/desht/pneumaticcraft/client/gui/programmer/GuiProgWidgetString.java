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

package me.desht.pneumaticcraft.client.gui.programmer;

import me.desht.pneumaticcraft.client.gui.GuiProgrammer;
import me.desht.pneumaticcraft.client.gui.widget.WidgetTextField;
import me.desht.pneumaticcraft.common.progwidgets.ProgWidgetText;

public class GuiProgWidgetString<T extends ProgWidgetText> extends GuiProgWidgetOptionBase<T> {
    private WidgetTextField textfield;

    public GuiProgWidgetString(T widget, GuiProgrammer guiProgrammer) {
        super(widget, guiProgrammer);
    }

    @Override
    public void init() {
        super.init();

        textfield = new WidgetTextField(font, guiLeft + 10, guiTop + 20, 160, 10) {
            @Override
            public boolean charTyped(char c, int keyCode) {
                if (c == '\n') {
                    removed();
                    minecraft.player.closeContainer();
                    return true;
                } else {
                    return super.charTyped(c, keyCode);
                }
            }
        };
        textfield.setMaxLength(1000);
        textfield.setValue(progWidget.string);
        textfield.setFocus(true);
        setFocused(textfield);
        addButton(textfield);
    }

    @Override
    public void removed() {
        progWidget.string = textfield.getValue();

        super.removed();
    }
}
