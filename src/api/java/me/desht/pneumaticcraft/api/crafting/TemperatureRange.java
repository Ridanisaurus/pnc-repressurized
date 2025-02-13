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

package me.desht.pneumaticcraft.api.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import org.apache.commons.lang3.Validate;

/**
 * Defines a valid operating temperature range for machines which use heat.  Temperatures are in Kelvin (i.e. absolute),
 * so negative values are not accepted.
 */
public class TemperatureRange {
    private static final TemperatureRange INVALID = new TemperatureRange(0, 1) {
        @Override
        public boolean inRange(int temp) {
            return false;
        }

        @Override
        public boolean inRange(double temp) {
            return false;
        }
    };
    private static final TemperatureRange ANY = new TemperatureRange(0, Integer.MAX_VALUE);

    private final int min;
    private final int max;

    private TemperatureRange(int min, int max) {
        Validate.isTrue(min >= 0 && max >= 0, "negative temperatures are not accepted!");
        Validate.isTrue(min < max, "min temp must be < max temp!");
        this.min = min;
        this.max = max;
    }

    /**
     * Get the minimum temperature for this range.
     *
     * @return the minimum temperature
     */
    public int getMin() {
        return min;
    }

    /**
     * Get the maximum temperature for this range.
     *
     * @return the maximum temperature
     */
    public int getMax() {
        return max;
    }

    /**
     * Get a specific temperature range.
     *
     * @param minTemp an inclusive minimum temperature
     * @param maxTemp an inclusive maximum temperature
     * @return a temperature range
     */
    public static TemperatureRange of(int minTemp, int maxTemp) {
        return minTemp == 0 && maxTemp == Integer.MAX_VALUE ? any() : new TemperatureRange(minTemp, maxTemp);
    }

    /**
     * Special "don't care" temperature range which always accepts any temperature.
     *
     * @return a temperature range
     */
    public static TemperatureRange any() {
        return ANY;
    }

    /**
     * Get a temperature range which accepts temperatures higher than or equal to the supplied value.
     *
     * @param minTemp an inclusive minimum temperature
     * @return a temperature range
     */
    public static TemperatureRange min(int minTemp) {
        return new TemperatureRange(minTemp, Integer.MAX_VALUE);
    }

    /**
     * Get a temperature range which accepts temperatures lower than or equal to the supplied value.
     *
     * @param maxTemp an inclusive maximum temperature
     * @return a temperature range
     */
    public static TemperatureRange max(int maxTemp) {
        return new TemperatureRange(0, maxTemp);
    }

    /**
     * Read a temperature range from packet buffer, as written by {@link #write(PacketBuffer)}
     * @param buffer the buffer
     * @return a new temperature range object
     */
    public static TemperatureRange read(PacketBuffer buffer) {
        return new TemperatureRange(buffer.readVarInt(), buffer.readVarInt());
    }

    /**
     * Check if the given temperature is valid for this range object.
     *
     * @param temp the temperature
     * @return true if valid, false otherwise
     */
    public boolean inRange(int temp) {
        return temp >= min && temp <= max;
    }

    /**
     * Check if the given temperature is valid for this range object.
     *
     * @param temp the temperature
     * @return true if valid, false otherwise
     */
    public boolean inRange(double temp) {
        return (int)temp >= min && (int)temp <= max;
    }

    /**
     * Get a temperature range which never accepts any temperature as valid.
     * @return a temperature range
     */
    public static TemperatureRange invalid() {
        return INVALID;
    }

    /**
     * Check if this temperature is the special "don't care" temperature, as returned by {@link #any()}.
     *
     * @return true if this temperature is a "don't care".
     */
    public boolean isAny() {
        return this == TemperatureRange.ANY;
    }

    public boolean hasMin() {
        return min > 0;
    }

    public boolean hasMax() {
        return max < Integer.MAX_VALUE;
    }

    public void write(PacketBuffer buffer) {
        buffer.writeVarInt(min);
        buffer.writeVarInt(max);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        if (min > -Integer.MAX_VALUE) json.addProperty("min_temp", min);
        if (max < Integer.MAX_VALUE) json.addProperty("max_temp", max);
        return json;
    }

    public static TemperatureRange fromJson(JsonObject json) {
        if (!json.has("min_temp") && !json.has("max_temp")) {
            return TemperatureRange.any();
        }
        if (!json.has("min_temp")) return TemperatureRange.max(JSONUtils.getAsInt(json,"max_temp"));
        if (!json.has("max_temp")) return TemperatureRange.min(JSONUtils.getAsInt(json,"min_temp"));
        return TemperatureRange.of(JSONUtils.getAsInt(json,"min_temp"), JSONUtils.getAsInt(json,"max_temp"));
    }

    public String asString(TemperatureScale scale) {
        if (isAny()) return "any";
        if (this == invalid()) return "invalid";

        if (min > -Integer.MAX_VALUE) {
            if (max < Integer.MAX_VALUE) {
                return scale.convertFromKelvin(min) + scale.symbol() + " - " + scale.convertFromKelvin(max) + scale.symbol();
            } else {
                return ">= " + scale.convertFromKelvin(min) + scale.symbol();
            }
        } else {
            if (max < Integer.MAX_VALUE) {
                return "<= " + scale.convertFromKelvin(max) + scale.symbol();
            } else {
                return "any";
            }
        }
    }

    public enum TemperatureScale {
        KELVIN("K"),
        CELSIUS("°C"),
        FAHRENHEIT("°F");

        private final String symbol;

        TemperatureScale(String symbol) {
            this.symbol = symbol;
        }

        public float convertFromKelvin(float tempIn) {
            switch (this) {
                case CELSIUS: return tempIn - 273;
                case FAHRENHEIT: return (tempIn - 273) * 1.8f + 32;
                case KELVIN: default: return tempIn;
            }
        }

        public String symbol() {
            return symbol;
        }
    }
}
