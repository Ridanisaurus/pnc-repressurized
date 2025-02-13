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

package me.desht.pneumaticcraft.common.item;

import me.desht.pneumaticcraft.api.lib.NBTKeys;
import me.desht.pneumaticcraft.api.misc.Symbols;
import me.desht.pneumaticcraft.client.util.ClientUtils;
import me.desht.pneumaticcraft.common.entity.semiblock.EntityLogisticsFrame;
import me.desht.pneumaticcraft.common.inventory.ContainerLogistics;
import me.desht.pneumaticcraft.common.semiblock.ItemSemiBlock;
import me.desht.pneumaticcraft.common.util.PneumaticCraftUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

import static me.desht.pneumaticcraft.api.misc.Symbols.bullet;
import static me.desht.pneumaticcraft.common.util.PneumaticCraftUtils.xlate;

public abstract class ItemLogisticsFrame extends ItemSemiBlock {
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand handIn) {
        ItemStack stack = player.getItemInHand(handIn);
        if (!world.isClientSide) {
            NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
                @Override
                public ITextComponent getDisplayName() {
                    return stack.getHoverName();
                }

                @Override
                public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                    return new ContainerLogistics(getContainerType(), i, playerInventory, -1);
                }
            }, (buffer) -> buffer.writeVarInt(-1));
        }
        return ActionResult.success(stack);
    }

    protected abstract ContainerType<?> getContainerType();

    @Override
    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> curInfo, ITooltipFlag extraInfo) {
        super.appendHoverText(stack, worldIn, curInfo, extraInfo);

        addLogisticsTooltip(stack, worldIn, curInfo, ClientUtils.hasShiftDown());
    }

    public static void addLogisticsTooltip(ItemStack stack, World world, List<ITextComponent> curInfo, boolean sneaking) {
        if (stack.getTag() != null && stack.getTag().contains(NBTKeys.ENTITY_TAG) && stack.getItem() instanceof ItemSemiBlock) {
            if (sneaking) {
                CompoundNBT tag = stack.getTag().getCompound(NBTKeys.ENTITY_TAG);
                if (tag.getBoolean(EntityLogisticsFrame.NBT_INVISIBLE)) {
                    curInfo.add(bullet().append(xlate("pneumaticcraft.gui.logistics_frame.invisible")).withStyle(TextFormatting.YELLOW));
                }
                if (tag.getBoolean(EntityLogisticsFrame.NBT_MATCH_DURABILITY)) {
                    curInfo.add(bullet().append(xlate("pneumaticcraft.gui.logistics_frame.matchDurability")).withStyle(TextFormatting.YELLOW));
                }
                if (tag.getBoolean(EntityLogisticsFrame.NBT_MATCH_NBT)) {
                    curInfo.add(bullet().append(xlate("pneumaticcraft.gui.logistics_frame.matchNBT")).withStyle(TextFormatting.YELLOW));
                }
                if (tag.getBoolean(EntityLogisticsFrame.NBT_MATCH_MODID)) {
                    curInfo.add(bullet().append(xlate("pneumaticcraft.gui.logistics_frame.matchModId")).withStyle(TextFormatting.YELLOW));
                }

                boolean whitelist = tag.getBoolean(EntityLogisticsFrame.NBT_ITEM_WHITELIST);
                curInfo.add(xlate("pneumaticcraft.gui.logistics_frame." + (whitelist ? "itemWhitelist" : "itemBlacklist"))
                        .append(":").withStyle(TextFormatting.YELLOW));

                ItemStackHandler handler = new ItemStackHandler();
                handler.deserializeNBT(tag.getCompound(EntityLogisticsFrame.NBT_ITEM_FILTERS));
                ItemStack[] stacks = new ItemStack[handler.getSlots()];
                for (int i = 0; i < handler.getSlots(); i++) {
                    stacks[i] = handler.getStackInSlot(i);
                }
                int l = curInfo.size();
                PneumaticCraftUtils.summariseItemStacks(curInfo, stacks, TextFormatting.GOLD + Symbols.BULLET + " ");
                if (curInfo.size() == l) curInfo.add(bullet().withStyle(TextFormatting.GOLD).append(xlate("pneumaticcraft.gui.misc.no_items").withStyle(TextFormatting.GOLD, TextFormatting.ITALIC)));
                l = curInfo.size();


                whitelist = tag.getBoolean(EntityLogisticsFrame.NBT_FLUID_WHITELIST);
                curInfo.add(xlate("pneumaticcraft.gui.logistics_frame." + (whitelist ? "fluidWhitelist" : "fluidBlacklist"))
                        .append(":").withStyle(TextFormatting.YELLOW));

                EntityLogisticsFrame.FluidFilter fluidFilter = new EntityLogisticsFrame.FluidFilter();
                fluidFilter.deserializeNBT(tag.getCompound(EntityLogisticsFrame.NBT_FLUID_FILTERS));
                for (int i = 0; i < fluidFilter.size(); i++) {
                    FluidStack fluid = fluidFilter.get(i);
                    if (!fluid.isEmpty()) {
                        curInfo.add(bullet().append(fluid.getAmount() + "mB ").append(fluid.getDisplayName()).withStyle(TextFormatting.GOLD));
                    }
                }
                if (curInfo.size() == l) curInfo.add(bullet().withStyle(TextFormatting.GOLD).append(xlate("pneumaticcraft.gui.misc.no_fluids").withStyle(TextFormatting.GOLD, TextFormatting.ITALIC)));
            } else {
                curInfo.add(xlate("pneumaticcraft.gui.logistics_frame.hasFilters"));
            }
        }
    }

}
