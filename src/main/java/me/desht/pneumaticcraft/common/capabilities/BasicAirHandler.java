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

package me.desht.pneumaticcraft.common.capabilities;

import me.desht.pneumaticcraft.api.tileentity.IAirHandler;
import me.desht.pneumaticcraft.common.network.GuiSynced;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * A reference implementation.  Subclass this or implement your own.
 */
public class BasicAirHandler implements IAirHandler, INBTSerializable<CompoundNBT> {
    @GuiSynced
    private int baseVolume;
    @GuiSynced
    private int airAmount;

    public BasicAirHandler(int volume) {
        this.baseVolume = volume;
        this.airAmount = 0;
    }

    @Override
    public float getPressure() {
        return (float) airAmount / getVolume();
    }

    @Override
    public int getAir() {
        return airAmount;
    }

    @Override
    public void addAir(int amount) {
        // floor at -1 bar, which is a hard vacuum
        airAmount = Math.max(airAmount + amount, -getVolume());
    }

    @Override
    public int getBaseVolume() {
        return baseVolume;
    }

    @Override
    public void setBaseVolume(int baseVolume) {
        this.baseVolume = baseVolume;
    }

    @Override
    public int getVolume() {
        return baseVolume;
    }

    @Override
    public float maxPressure() {
        return 10f;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("Air", getAir());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        airAmount = nbt.getInt("Air");
    }
}
