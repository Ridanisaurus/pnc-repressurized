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

package me.desht.pneumaticcraft.common.semiblock;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

import java.util.Objects;

public interface IProvidingInventoryListener {
    void notify(TileEntityAndFace teAndFace);

    class TileEntityAndFace {
        private final TileEntity te;
        private final Direction face;

        public TileEntityAndFace(TileEntity te, Direction face) {
            this.te = te;
            this.face = face;
        }

        public TileEntity getTileEntity() {
            return te;
        }

        public Direction getFace() {
            return face;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TileEntityAndFace)) return false;
            TileEntityAndFace tileEntityAndFace = (TileEntityAndFace) o;
            return te.equals(tileEntityAndFace.te) &&
                    face == tileEntityAndFace.face;
        }

        @Override
        public int hashCode() {
            return Objects.hash(te, face);
        }
    }
}

