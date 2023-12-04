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

package me.desht.pneumaticcraft.common.network;

import me.desht.pneumaticcraft.client.pneumatic_armor.upgrade_handler.DroneDebugClientHandler;
import me.desht.pneumaticcraft.common.drone.IDroneBase;
import me.desht.pneumaticcraft.common.drone.progwidgets.IProgWidget;
import me.desht.pneumaticcraft.common.drone.progwidgets.WidgetSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * Received on: CLIENT
 * Sent by server to sync a (debugged) drone's programming widgets
 */
public class PacketSyncDroneEntityProgWidgets extends PacketDroneDebugBase {
    private final List<IProgWidget> progWidgets;

    public PacketSyncDroneEntityProgWidgets(IDroneBase drone) {
        super(drone);
        progWidgets = drone.getActiveAIManager().widgets();
    }

    PacketSyncDroneEntityProgWidgets(FriendlyByteBuf buf) {
        super(buf);
        progWidgets = WidgetSerializer.readWidgetsFromPacket(buf);
    }

    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        WidgetSerializer.writeProgWidgetsToPacket(progWidgets, buf);
    }

    @Override
    void handle(Player player, IDroneBase droneBase) {
        List<IProgWidget> widgets = droneBase.getProgWidgets();
        widgets.clear();
        widgets.addAll(progWidgets);
        DroneDebugClientHandler.onWidgetsChanged();
    }
}
