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

package me.desht.pneumaticcraft.common.block.tubes;

import me.desht.pneumaticcraft.api.PNCCapabilities;
import me.desht.pneumaticcraft.client.render.area.AreaRenderManager;
import me.desht.pneumaticcraft.common.entity.semiblock.EntitySemiblockBase;
import me.desht.pneumaticcraft.common.item.ItemTubeModule;
import me.desht.pneumaticcraft.common.network.NetworkHandler;
import me.desht.pneumaticcraft.common.network.PacketUpdatePressureBlock;
import me.desht.pneumaticcraft.common.particle.AirParticleData;
import me.desht.pneumaticcraft.common.tileentity.RangeManager;
import me.desht.pneumaticcraft.common.tileentity.TileEntityHeatSink;
import me.desht.pneumaticcraft.common.util.DirectionUtil;
import me.desht.pneumaticcraft.common.util.EntityFilter;
import me.desht.pneumaticcraft.common.util.PneumaticCraftUtils;
import me.desht.pneumaticcraft.lib.PneumaticValues;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.*;

import static me.desht.pneumaticcraft.common.util.PneumaticCraftUtils.xlate;

public class ModuleAirGrate extends TubeModule {
    private int grateRange;
    private boolean vacuum;
    private final Set<TileEntityHeatSink> heatSinks = new HashSet<>();
    private boolean showRange;
    private EntityFilter entityFilter = null;
    private final Map<BlockPos,Boolean> traceabilityCache = new HashMap<>();

    private LazyOptional<IItemHandler> itemInsertionCap = null; // null = "unknown", LazyOptional.empty() = "known absent"
    private LazyOptional<IFluidHandler> fluidInsertionCap = null;

    public ModuleAirGrate(ItemTubeModule itemTubeModule) {
        super(itemTubeModule);
    }

    @Override
    public double getWidth() {
        return 16D;
    }

    @Override
    public void update() {
        super.update();

        World world = pressureTube.getLevel();
        BlockPos pos = pressureTube.getBlockPos();

        if ((world.getGameTime() & 0x1f) == 0) traceabilityCache.clear();

        int oldGrateRange = grateRange;
        grateRange = calculateRange();
        if (oldGrateRange != grateRange) {
            if (!world.isClientSide) {
                getTube().getCapability(PNCCapabilities.AIR_HANDLER_MACHINE_CAPABILITY)
                        .ifPresent(h -> NetworkHandler.sendToAllTracking(new PacketUpdatePressureBlock(getTube(), null, h.getSideLeaking(), h.getAir()), getTube()));
            } else {
                if (showRange) {
                    AreaRenderManager.getInstance().showArea(RangeManager.getFrame(getAffectedAABB()), 0x60FFC060, pressureTube, false);
                }
            }
        }

        if (!world.isClientSide) coolHeatSinks();
        Vector3d tileVec = Vector3d.atCenterOf(pos).add(getDirection().getStepX() * 0.49, getDirection().getStepY() * 0.49, getDirection().getStepZ() * 0.49);
        pushEntities(world, pos, tileVec);
    }

    @Override
    public void onNeighborBlockUpdate() {
        itemInsertionCap = null;
        fluidInsertionCap = null;
    }

    private AxisAlignedBB getAffectedAABB() {
        BlockPos pos = pressureTube.getBlockPos().relative(getDirection(), grateRange + 1);
        return new AxisAlignedBB(pos).inflate(grateRange);
    }

    private int calculateRange() {
        float range = pressureTube.getPressure() * 4f;
        vacuum = range < 0;
        if (vacuum) range *= -4f;
        return (int) range;
    }

    private void pushEntities(World world, BlockPos pos, Vector3d traceVec) {
        AxisAlignedBB bbBox = getAffectedAABB();
        List<Entity> entities = world.getEntitiesOfClass(Entity.class, bbBox, entityFilter);
        double d0 = grateRange * 3;
        int entitiesMoved = 0;
        for (Entity entity : entities) {
            if (ignoreEntity(entity) || !entity.isAlive() || !rayTraceOK(entity, traceVec)) {
                continue;
            }
            if (!entity.level.isClientSide) {
                tryInsertion(traceVec, entity);
            }
            double x = (entity.getX() - pos.getX() - 0.5D) / d0;
            double y = (entity.getY() + entity.getEyeHeight() - pos.getY() - 0.5D) / d0;
            BlockPos entityPos = entity.blockPosition();
            if (!Block.canSupportCenter(world, entityPos, Direction.UP) && !world.isEmptyBlock(entityPos)) {
                y -= 0.15;  // kludge: avoid entities getting stuck on edges, e.g. farmland->full block
            }
            double z = (entity.getZ() - pos.getZ() - 0.5D) / d0;
            double d4 = Math.sqrt(x * x + y * y + z * z);
            double d5 = 1.0D - d4;

            if (d5 > 0.0D) {
                d5 *= d5;
                if (vacuum) d5 *= -1;
                if (entity.isOnGround()) entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.25, 0));
                entity.move(MoverType.SELF, new Vector3d(x * d5, y * d5, z * d5));
                entitiesMoved++;
                if (world.isClientSide && world.random.nextDouble() < 0.2) {
                    if (vacuum) {
                        world.addParticle(AirParticleData.DENSE, entity.getX(), entity.getY(), entity.getZ(), -x, -y, -z);
                    } else {
                        world.addParticle(AirParticleData.DENSE, pos.getX() + 0.5 + getDirection().getStepX(), pos.getY() + 0.5 + getDirection().getStepY(), pos.getZ() + 0.5 + getDirection().getStepZ(), x, y, z);
                    }
                }
            }
        }
        if (!world.isClientSide) {
            int usage = pressureTube.getPressure() > 0 ? -PneumaticValues.USAGE_AIR_GRATE : PneumaticValues.USAGE_AIR_GRATE;
            pressureTube.addAir(entitiesMoved * usage);
        }
    }

    private void tryInsertion(Vector3d traceVec, Entity entity) {
        if (entity instanceof ItemEntity && isCloseEnough(entity, traceVec)) {
            tryItemInsertion((ItemEntity) entity);
        } else if (entity instanceof ExperienceOrbEntity && isCloseEnough(entity, traceVec)) {
            tryOrbInsertion((ExperienceOrbEntity) entity);
        }
    }

    private void tryItemInsertion(ItemEntity entity) {
        ItemStack stack = entity.getItem();
        getItemInsertionCap().ifPresent(handler -> {
            ItemStack excess = ItemHandlerHelper.insertItem(handler, stack, false);
            if (excess.isEmpty()) {
                entity.remove();
            } else {
                entity.setItem(excess);
            }
        });
    }

    private void tryOrbInsertion(ExperienceOrbEntity entity) {
        getFluidInsertionCap().ifPresent(handler -> {
            if (PneumaticCraftUtils.fillTankWithOrb(handler, entity, IFluidHandler.FluidAction.EXECUTE)) {
                entity.remove();
            }
        });
    }

    private boolean isCloseEnough(Entity entity, Vector3d traceVec) {
        return entity.distanceToSqr(traceVec) < 1D;
    }

    private boolean ignoreEntity(Entity entity) {
        if (entity instanceof PlayerEntity) {
            return ((PlayerEntity) entity).isCreative() || entity.isShiftKeyDown() || entity.isSpectator();
        }
        if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity) {
            return false;
        }
        // don't touch semiblocks, at all
        return !entity.isPushable() || entity instanceof EntitySemiblockBase;
    }

    private boolean rayTraceOK(Entity entity, Vector3d traceVec) {
        BlockPos pos = new BlockPos(entity.getEyePosition(0f));
        return traceabilityCache.computeIfAbsent(pos, k -> {
            Vector3d entityVec = new Vector3d(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ());
            RayTraceContext ctx = new RayTraceContext(entityVec, traceVec, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity);
            BlockRayTraceResult trace = entity.getCommandSenderWorld().clip(ctx);
            return trace.getBlockPos().equals(pressureTube.getBlockPos());
        });
    }

    private LazyOptional<IItemHandler> getItemInsertionCap() {
        if (itemInsertionCap == null) {
            for (Direction dir : DirectionUtil.VALUES) {
                TileEntity te = pressureTube.getLevel().getBlockEntity(pressureTube.getBlockPos().relative(dir));
                if (te != null) {
                    LazyOptional<IItemHandler> cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite());
                    // bit of a kludge: exclude TE's which also offer a fluid capability on this side
                    if (cap.isPresent() && !te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite()).isPresent()) {
                        itemInsertionCap = cap;
                        itemInsertionCap.addListener(l -> itemInsertionCap = null);
                        break;
                    }
                }
            }
            if (itemInsertionCap == null) itemInsertionCap = LazyOptional.empty();
        }
        return itemInsertionCap;
    }

    private LazyOptional<IFluidHandler> getFluidInsertionCap() {
        if (fluidInsertionCap == null) {
            for (Direction dir : DirectionUtil.VALUES) {
                TileEntity te = pressureTube.getLevel().getBlockEntity(pressureTube.getBlockPos().relative(dir));
                if (te != null) {
                    LazyOptional<IFluidHandler> cap = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite());
                    if (cap.isPresent()) {
                        fluidInsertionCap = cap;
                        fluidInsertionCap.addListener(l -> fluidInsertionCap = null);
                        break;
                    }
                }
            }
            if (fluidInsertionCap == null) fluidInsertionCap = LazyOptional.empty();
        }
        return fluidInsertionCap;
    }

    private void coolHeatSinks() {
        if (grateRange >= 2) {
            int curTeIndex = (int) (pressureTube.getLevel().getGameTime() % 27);
            BlockPos curPos = pressureTube.getBlockPos().relative(dir, 2).offset(-1 + curTeIndex % 3, -1 + curTeIndex / 3 % 3, -1 + curTeIndex / 9 % 3);
            TileEntity te = pressureTube.getLevel().getBlockEntity(curPos);
            if (te instanceof TileEntityHeatSink) heatSinks.add((TileEntityHeatSink) te);

            Iterator<TileEntityHeatSink> iterator = heatSinks.iterator();
            int tubesCooled = 0;
            while (iterator.hasNext()) {
                TileEntityHeatSink heatSink = iterator.next();
                if (heatSink.isRemoved()) {
                    iterator.remove();
                } else {
                    for (int i = 0; i < 4; i++) {
                        heatSink.onFannedByAirGrate();
                    }
                    tubesCooled++;
                }
            }
            if (tubesCooled > 0) {
                pressureTube.addAir(-(5 + (tubesCooled / 3)));
            }
        }
    }

    @Override
    public void readFromNBT(CompoundNBT tag) {
        super.readFromNBT(tag);
        vacuum = tag.getBoolean("vacuum");
        grateRange = tag.getInt("grateRange");
        String f = tag.getString("entityFilter");
        entityFilter = f.isEmpty() ? null : EntityFilter.fromString(f);
    }

    @Override
    public CompoundNBT writeToNBT(CompoundNBT tag) {
        super.writeToNBT(tag);
        tag.putBoolean("vacuum", vacuum);
        tag.putInt("grateRange", grateRange);
        tag.putString("entityFilter", entityFilter == null ? "" : entityFilter.toString());
        return tag;
    }

    @Override
    public void addInfo(List<ITextComponent> curInfo) {
        super.addInfo(curInfo);
        String k = grateRange == 0 ? "idle" : vacuum ? "attracting" : "repelling";
        curInfo.add(xlate("pneumaticcraft.waila.airGrateModule." + k).withStyle(TextFormatting.WHITE));
        if (grateRange != 0) {
            curInfo.add(xlate("pneumaticcraft.message.misc.range", grateRange).withStyle(TextFormatting.WHITE));
        }
        if (entityFilter != null) {
            curInfo.add(xlate("pneumaticcraft.gui.entityFilter.show", entityFilter.toString()).withStyle(TextFormatting.WHITE));
        }
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pressureTube.getBlockPos().relative(getDirection(), grateRange + 1)).inflate(grateRange * 2);
    }

    public String getEntityFilterString() {
        return entityFilter == null ? "" : entityFilter.toString();
    }

    public void setEntityFilter(String filter) {
        entityFilter = EntityFilter.fromString(filter);
    }

    public boolean isShowRange() {
        return showRange;
    }

    public void setShowRange(boolean showRange) {
        this.showRange = showRange;
        if (showRange) {
            AreaRenderManager.getInstance().showArea(RangeManager.getFrame(getAffectedAABB()), 0x60FFC060, pressureTube, false);
        } else {
            AreaRenderManager.getInstance().removeHandlers(pressureTube);
        }
    }

    @Override
    public void onRemoved() {
        if (pressureTube.getLevel().isClientSide) {
            AreaRenderManager.getInstance().removeHandlers(pressureTube);
        }
    }
}
