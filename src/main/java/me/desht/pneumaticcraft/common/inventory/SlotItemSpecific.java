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

package me.desht.pneumaticcraft.common.inventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

class SlotItemSpecific extends SlotItemHandler {
    private final Predicate<ItemStack> itemAllowed;

    SlotItemSpecific(IItemHandler handler, Predicate<ItemStack> itemAllowed, int index, int x, int y) {
        super(handler, index, x, y);
        this.itemAllowed = itemAllowed;
    }

    public SlotItemSpecific(IItemHandler handler, Item item, int index, int x, int y) {
        super(handler, index, x, y);
        this.itemAllowed = stack -> stack.getItem() == item;
    }

//    SlotItemSpecific(IItemHandler handler, Item itemAllowed, int index, int x, int y) {
//        super(handler, index, x, y);
//        this.itemAllowed = itemAllowed;
//        this.oreDictEntry = 0;
//        this.dye = false;
//    }

//    SlotItemSpecific(IItemHandler handler, String oreDictKeyAllowed, int index, int x, int y) {
//        super(handler, index, x, y);
//        this.itemAllowed = null;
//        this.oreDictEntry = OreDictionary.getOreID(oreDictKeyAllowed);
//        this.dye = oreDictKeyAllowed.equals("dye");
//    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for
     * the armor slots.
     */
    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return stack.isEmpty() || itemAllowed.test(stack);
    }

}
