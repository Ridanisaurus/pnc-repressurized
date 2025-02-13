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

import com.mojang.datafixers.util.Pair;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SlotPlayer extends Slot {
    private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{
            PlayerContainer.EMPTY_ARMOR_SLOT_BOOTS,
            PlayerContainer.EMPTY_ARMOR_SLOT_LEGGINGS,
            PlayerContainer.EMPTY_ARMOR_SLOT_CHESTPLATE,
            PlayerContainer.EMPTY_ARMOR_SLOT_HELMET
    };
    private final EquipmentSlotType slotType;

    public SlotPlayer(PlayerInventory inventoryIn, EquipmentSlotType slotType, int xPosition, int yPosition) {
        super(inventoryIn, getIndexForSlot(slotType), xPosition, yPosition);
        this.slotType = slotType;
    }

    @Override
    public int getMaxStackSize() {
        return slotType == EquipmentSlotType.OFFHAND ? super.getMaxStackSize() : 1;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return slotType == EquipmentSlotType.OFFHAND ? super.mayPlace(stack) : stack.canEquip(slotType, ((PlayerInventory) container).player);
    }

    @Override
    public boolean mayPickup(PlayerEntity playerIn) {
        if (slotType == EquipmentSlotType.OFFHAND) return super.mayPickup(playerIn);

        ItemStack itemstack = this.getItem();
        return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.mayPickup(playerIn);
    }

    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return slotType.getType() == EquipmentSlotType.Group.ARMOR ?
                Pair.of(PlayerContainer.BLOCK_ATLAS, ARMOR_SLOT_TEXTURES[slotType.getIndex()]) :
                Pair.of(PlayerContainer.BLOCK_ATLAS, PlayerContainer.EMPTY_ARMOR_SLOT_SHIELD);
    }

    private static int getIndexForSlot(EquipmentSlotType type) {
        if (type.getType() == EquipmentSlotType.Group.ARMOR) {
            return 36 + type.getIndex();
        } else if (type == EquipmentSlotType.OFFHAND) {
            return 40;
        } else {
            throw new IllegalArgumentException("invalid equipment slot: " + type);
        }
    }
}
