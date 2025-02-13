/*
 * This file is part of pnc-repressurized API.
 *
 *     pnc-repressurized API is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     pnc-repressurized is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with pnc-repressurized API.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.desht.pneumaticcraft.api.tileentity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public interface IManoMeasurable {
    /**
     * This method is invoked by the Manometer when a player right-clicks a TE or Entity with this interface implemented.
     *
     * @param player  player who is right-clicking the measurable TE, and therefore needs to get the message
     * @param curInfo list you can append info to. If you don't append any info no air will be used.
     */
    void printManometerMessage(PlayerEntity player, List<ITextComponent> curInfo);
}
