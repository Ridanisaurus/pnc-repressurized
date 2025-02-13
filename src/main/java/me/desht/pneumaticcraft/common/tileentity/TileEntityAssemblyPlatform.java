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

package me.desht.pneumaticcraft.common.tileentity;

import me.desht.pneumaticcraft.common.core.ModTileEntities;
import me.desht.pneumaticcraft.common.inventory.handler.BaseItemStackHandler;
import me.desht.pneumaticcraft.common.network.DescSynced;
import me.desht.pneumaticcraft.common.network.LazySynced;
import me.desht.pneumaticcraft.common.recipes.assembly.AssemblyProgram;
import me.desht.pneumaticcraft.lib.TileEntityConstants;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class TileEntityAssemblyPlatform extends TileEntityTickableBase implements IAssemblyMachine, IResettable {
    @DescSynced
    private boolean shouldClawClose;
    @DescSynced
    @LazySynced
    public float clawProgress;
    public float oldClawProgress;
    @DescSynced
    private final BaseItemStackHandler itemHandler = new BaseItemStackHandler(this, 1);
    private float speed = 1.0F;
    private BlockPos controllerPos;

    public TileEntityAssemblyPlatform() {
        super(ModTileEntities.ASSEMBLY_PLATFORM.get());
    }

    @Override
    public void tick() {
        super.tick();
        oldClawProgress = clawProgress;
        if (!shouldClawClose && clawProgress > 0F) {
            clawProgress = Math.max(clawProgress - TileEntityConstants.ASSEMBLY_IO_UNIT_CLAW_SPEED * speed, 0);
        } else if (shouldClawClose && clawProgress < 1F) {
            clawProgress = Math.min(clawProgress + TileEntityConstants.ASSEMBLY_IO_UNIT_CLAW_SPEED * speed, 1);
        }
    }

    private boolean isClawDone() {
        return clawProgress == (shouldClawClose ? 1F : 0F);
    }

    @Override
    public boolean isIdle() {
        return !shouldClawClose && isClawDone() && getHeldStack().isEmpty();
    }

    @Override
    public boolean reset() {
        openClaw();
        return isIdle();
    }

    boolean closeClaw() {
        shouldClawClose = true;
        sendDescriptionPacket();
        return isClawDone();
    }

    boolean openClaw() {
        shouldClawClose = false;
        sendDescriptionPacket();
        return isClawDone();
    }

    @Nonnull
    public ItemStack getHeldStack() {
        return itemHandler.getStackInSlot(0);
    }

    public void setHeldStack(@Nonnull ItemStack stack) {
        itemHandler.setStackInSlot(0, stack);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        tag.putBoolean("clawClosing", shouldClawClose);
        tag.putFloat("clawProgress", clawProgress);
        tag.putFloat("speed", speed);
        tag.put("Items", itemHandler.serializeNBT());
        return tag;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);

        shouldClawClose = tag.getBoolean("clawClosing");
        clawProgress = tag.getFloat("clawProgress");
        speed = tag.getFloat("speed");
        itemHandler.deserializeNBT(tag.getCompound("Items"));
    }

    @Override
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public AssemblyProgram.EnumMachine getAssemblyType() {
        return AssemblyProgram.EnumMachine.PLATFORM;
    }

    @Override
    public void setControllerPos(BlockPos controllerPos) {
        this.controllerPos = controllerPos;
    }

    @Override
    public void onNeighborBlockUpdate(BlockPos fromPos) {
        super.onNeighborBlockUpdate(fromPos);
        invalidateSystem();
    }

    @Override
    public IItemHandler getPrimaryInventory() {
        return itemHandler;
    }

    private void invalidateSystem() {
        if (controllerPos != null) {
            TileEntity te = getLevel().getBlockEntity(controllerPos);
            if (te instanceof TileEntityAssemblyController) {
                ((TileEntityAssemblyController) te).invalidateAssemblySystem();
            }
        }
    }
}
