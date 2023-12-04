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

package me.desht.pneumaticcraft.common.thirdparty.ae2;

public class AE2RequesterIntegration /*implements IGridBlock, IGridHost, ICraftingProvider, ICraftingWatcherHost,
        IStackWatcherHost, ICellContainer, IGridTickable, IMEInventoryHandler<IAEItemStack> */
{
//    private final EntityLogisticsRequester logisticsRequester;
//    private IGridNode gridNode;
//    private ICraftingGrid craftingGrid;
//    private IStackWatcher stackWatcher;
//    private ICraftingWatcher craftingWatcher;
//    private boolean needToCheckForInterface = true;
//    private final Set<TileEntityAndFace> providingInventories = new HashSet<>();
//
//    public AE2RequesterIntegration(EntityLogisticsRequester logisticsRequester) {
//        this.logisticsRequester = logisticsRequester;
//    }
//
//    public void maybeAddTE(TileEntityAndFace teAndFace) {
//        if (gridNode != null) {
//            providingInventories.add(teAndFace);
//        }
//    }
//
//    public void maybeCheckForInterface() {
//        if (needToCheckForInterface) {
//            if (logisticsRequester.isAE2enabled() && gridNode == null) {
//                needToCheckForInterface = checkForInterface();
//            } else {
//                needToCheckForInterface = false;
//            }
//        }
//    }
//
//    public boolean isPlacedOnInterface() {
//        return AE2PNCAddon.api.definitions().blocks().iface().maybeEntity()
//                .map(e -> e.isInstance(logisticsRequester.getCachedTileEntity()))
//                .orElse(false);
//    }
//
//    public void shutdown() {
//        if (gridNode != null) {
//            gridNode.destroy();
//            gridNode = null;
//        }
//    }
//
//    public void setEnabled(boolean ae2enabled) {
//        needToCheckForInterface = ae2enabled;
//        if (!ae2enabled && gridNode != null) {
//            shutdown();
//        }
//    }
//
//    private boolean checkForInterface() {
//        // note: returns false on success (= no more checking needed), trye
//        if (isPlacedOnInterface()) {
//            BlockEntity te = logisticsRequester.getCachedTileEntity();
//            if (te instanceof IGridHost) {
//                // grid node for the ME Interface
//                IGridNode nodeA = ((IGridHost) te).getGridNode(AEPartLocation.INTERNAL);
//                if (nodeA == null) return true;
//                // grid node for own Requester Frame
//                IGridNode nodeB = getGridNode(AEPartLocation.INTERNAL);
//                if (nodeB == null) return true;
//                try {
//                    AE2PNCAddon.api.grid().createGridConnection(nodeA, nodeB);
//                } catch (FailedConnectionException e) {
//                    Log.error("Couldn't connect to an ME Interface!");
//                    e.printStackTrace();
//                }
//            }
//        }
//        return false;
//    }
//
//    public AECableType getCableConnectionType(AEPartLocation arg0) {
//        return AECableType.NONE;
//    }
//
//    @Override
//    public void securityBreak() {
//        logisticsRequester.remove();
//    }
//
//    @Override
//    public IGridNode getGridNode(AEPartLocation d) {
//        if (gridNode == null) {
//            gridNode = AE2PNCAddon.api.grid().createGridNode(this);
//        }
//        return gridNode;
//    }
//
//    @Override
//    public double getIdlePowerUsage() {
//        return 1;
//    }
//
//    @Nonnull
//    @Override
//    public EnumSet<GridFlags> getFlags() {
//        return EnumSet.noneOf(GridFlags.class);
//    }
//
//    @Override
//    public boolean isWorldAccessible() {
//        return false;
//    }
//
//    @Nonnull
//    @Override
//    public DimensionalCoord getLocation() {
//        return new DimensionalCoord(logisticsRequester.getWorld(), logisticsRequester.getBlockPos());
//    }
//
//    @Nonnull
//    @Override
//    public AEColor getGridColor() {
//        return AEColor.TRANSPARENT;
//    }
//
//    @Override
//    public void onGridNotification(@Nonnull GridNotification gridNotification) {
//    }
//
////    @Override
////    public void setNetworkStatus(IGrid iGrid, int i) {
////    }
//
//    @Nonnull
//    @Override
//    public EnumSet<Direction> getConnectableSides() {
//        return EnumSet.noneOf(Direction.class); //Shouldn't be called as isWorldAccessible is false.
//    }
//
//    @Nonnull
//    @Override
//    public IGridHost getMachine() {
//        return this;
//    }
//
//    @Override
//    public void gridChanged() {
//    }
//
//    @Nonnull
//    @Override
//    public ItemStack getMachineRepresentation() {
//        return new ItemStack(ModItems.LOGISTICS_FRAME_REQUESTER.get());
//    }
//
//    @Override
//    public void provideCrafting(ICraftingProviderHelper iCraftingProviderHelper) {
//        updateProvidingItems(iCraftingProviderHelper);
//    }
//
//    @Override
//    public boolean pushPattern(ICraftingPatternDetails iCraftingPatternDetails, CraftingContainer craftingInventory) {
//        return false;
//    }
//
//    @Override
//    public boolean isBusy() {
//        return true;
//    }
//
//    @Override
//    public void updateWatcher(ICraftingWatcher watcher) {
//        craftingWatcher = watcher;
//        updateProvidingItems(null);
//    }
//
//    @Override
//    public void onRequestChange(ICraftingGrid grid, IAEItemStack aeStack) {
//        craftingGrid = grid;
//        int freeSlot = -1;
//        for (int i = 0; i < getFilters().getSlots(); i++) {
//            ItemStack filterStack = getFilters().getStackInSlot(i);
//            if (!filterStack.isEmpty()) {
//                if (aeStack.isSameType(filterStack)) {
//                    filterStack.setCount((int) grid.requesting(aeStack));
//                    return;
//                }
//            } else if (freeSlot == -1) {
//                freeSlot = i;
//            }
//        }
//        if (freeSlot >= 0) {
//            // no item in the requester frame's filter: add it!
//            getFilters().setStackInSlot(freeSlot, aeStack.createItemStack());
//        }
//    }
//
//    @Nullable
//    @Override
//    public IGridNode getActionableNode() {
//        return getGridNode(null);
//    }
//
//    @Override
//    public void updateWatcher(IStackWatcher watcher) {
//        stackWatcher = watcher;
//        updateProvidingItems(null);
//    }
//
//    @Override
//    public void onStackChange(IItemList<?> iItemList, IAEStack<?> iaeStack, IAEStack<?> iaeStack1, IActionSource iActionSource, IStorageChannel<?> iStorageChannel) {
//        if (craftingGrid != null) {
//            for (int i = 0; i < getFilters().getSlots(); i++) {
//                ItemStack s = getFilters().getStackInSlot(i);
//                if (!s.isEmpty()) {
//                    if (!craftingGrid.isRequesting(AE2PNCAddon.api.storage().getStorageChannel(IItemStorageChannel.class).createStack(s))) {
//                        getFilters().setStackInSlot(i, ItemStack.EMPTY);
//                        notifyNetworkOfCraftingChange();
//                    }
//                }
//            }
//        }
//    }
//
//    private IItemHandlerModifiable getFilters() {
//        return logisticsRequester.getItemFilterHandler();
//    }
//
//    @Nonnull
//    @Override
//    public TickingRequest getTickingRequest(@Nonnull IGridNode iGridNode) {
//        return new TickingRequest(120, 120, false, false);
//    }
//
//    @Nonnull
//    @Override
//    public TickRateModulation tickingRequest(@Nonnull IGridNode iGridNode, int i) {
//        notifyNetworkOfCraftingChange();
//        if (gridNode != null) {
//            // Doing it on interval, as doing it right after  AE2.api.createGridConnection doesn't seem to work..
//            getGridNode(null).getGrid().postEvent(new MENetworkCellArrayUpdate());
//        }
//        return TickRateModulation.SAME;
//    }
//
//    @Override
//    public AccessRestriction getAccess() {
//        return AccessRestriction.READ;
//    }
//
//    @Override
//    public boolean isPrioritized(IAEItemStack iaeItemStack) {
//        return false;
//    }
//
//    @Override
//    public boolean canAccept(IAEItemStack iaeItemStack) {
//        return false;
//    }
//
//    @Override
//    public int getSlot() {
//        return 0;
//    }
//
//    @Override
//    public boolean validForPass(int i) {
//        return true;
//    }
//
//    @Override
//    public IAEItemStack injectItems(IAEItemStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
//        return iaeItemStack;
//    }
//
//    @Override
//    public IAEItemStack extractItems(IAEItemStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
//        return null;
//    }
//
//    @Override
//    public IItemList<IAEItemStack> getAvailableItems(IItemList<IAEItemStack> iItemList) {
//        for (IAEItemStack stack : getProvidingItems(true)) {
//            stack.setCountRequestable(stack.getStackSize());
//            iItemList.addRequestable(stack);
//        }
//        return iItemList;
//    }
//
//    @Override
//    public IStorageChannel<IAEItemStack> getChannel() {
//        return AE2PNCAddon.api.storage().getStorageChannel(IItemStorageChannel.class);
//    }
//
//    @Override
//    public void blinkCell(int i) {
//    }
//
//    @Override
//    public List<IMEInventoryHandler> getCellArray(IStorageChannel<?> channel) {
//        if (channel == AE2PNCAddon.api.storage().getStorageChannel(IItemStorageChannel.class)) {
//            return Collections.singletonList(this);
//        } else {
//            return new ArrayList<>();
//        }
//    }
//
//    @Override
//    public int getPriority() {
//        return 0;
//    }
//
//    @Override
//    public void saveChanges(@Nullable ICellInventory<?> iCellInventory) {
//    }
//
//    private void updateProvidingItems(ICraftingProviderHelper cHelper) {
//        if (stackWatcher != null) stackWatcher.reset();
//        if (craftingWatcher != null) craftingWatcher.reset();
//
//        // watch any items that are in providing inventories
//        for (IAEItemStack stack : getProvidingItems(false)) {
//            if (stackWatcher != null) stackWatcher.add(stack);
//            if (craftingWatcher != null) craftingWatcher.add(stack);
//            if (cHelper != null) cHelper.setEmitable(stack);
//        }
//        // and also watch any items that are in this requester's filter
//        for (int i = 0; i < logisticsRequester.getItemFilterHandler().getSlots(); i++) {
//            ItemStack stack = logisticsRequester.getItemFilterHandler().getStackInSlot(i);
//            if (!stack.isEmpty()) {
//                IAEItemStack iaeStack = AE2PNCAddon.api.storage().getStorageChannel(IItemStorageChannel.class).createStack(stack);
//                if (stackWatcher != null) stackWatcher.add(iaeStack);
//                if (craftingWatcher != null) craftingWatcher.add(iaeStack);
//                if (cHelper != null) cHelper.setEmitable(iaeStack);
//            }
//        }
//    }
//
//    private List<IAEItemStack> getProvidingItems(boolean initialList) {
//        List<IAEItemStack> stacks = new ArrayList<>();
//        Iterator<TileEntityAndFace> iter = providingInventories.iterator();
//        while (iter.hasNext()) {
//            TileEntityAndFace teFace = iter.next();
//            boolean ok = false;
//            if (isLogisticsTEvalid(teFace.getTileEntity())) {
//                ok = IOHelper.getInventoryForTE(teFace.getTileEntity(), teFace.getFace()).map(inv -> {
//                    for (int i = 0; i < inv.getSlots(); i++) {
//                        ItemStack stack = inv.getStackInSlot(i);
//                        if (!stack.isEmpty()) {
//                            IAEItemStack aeStack = AE2PNCAddon.api.storage().getStorageChannel(IItemStorageChannel.class).createStack(stack);
//                            if (aeStack != null) {
//                                stacks.add(initialList ? aeStack : aeStack.setStackSize(0).setCountRequestable(stack.getCount()));
//                            }
//                        }
//                    }
//                    return true;
//                }).orElse(false);
//            }
//            if (!ok) iter.remove();
//        }
//        return stacks;
//    }
//
//    private boolean isLogisticsTEvalid(BlockEntity te) {
//        return !te.isRemoved() && SemiblockTracker.getInstance().getAllSemiblocks(te.getLevel(), te.getBlockPos())
//                .filter(semiblock -> semiblock instanceof EntityLogisticsFrame)
//                .anyMatch(semiblock -> ((EntityLogisticsFrame) semiblock).shouldProvideTo(logisticsRequester.getPriority()));
//    }
//
//    private void notifyNetworkOfCraftingChange() {
//        if (gridNode != null) {
//            gridNode.getGrid().postEvent(new MENetworkCraftingPatternChange(this, gridNode));
//        }
//    }
}
