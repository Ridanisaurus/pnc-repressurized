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

package me.desht.pneumaticcraft.common.util;

import com.google.common.collect.Sets;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Set;

public class ItemTagMatcher {
    private final Set<ResourceLocation> tags;

    private ItemTagMatcher(ItemStack stack) {
        this.tags = stack.getItem().getTags();
    }

    public boolean match(ItemStack stack) {
        return !Sets.intersection(tags, stack.getItem().getTags()).isEmpty();
    }

    public static boolean matchTags(@Nonnull ItemStack stack1, @Nonnull ItemStack stack2) {
        return new ItemTagMatcher(stack1).match(stack2);
    }
}
