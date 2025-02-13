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

package me.desht.pneumaticcraft.common.thirdparty;

import com.google.common.collect.Maps;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Map;

/**
 * Maintains a cache of mod id's to "friendly" mod names.
 */
public class ModNameCache {
    private static final Map<String, String> id2name = Maps.newHashMap();

    public static void init() {
        ModList.get().forEachModFile(modFile -> {
            for (IModInfo info : modFile.getModInfos()) {
                id2name.put(info.getModId(), info.getDisplayName());
                id2name.put(info.getModId().toLowerCase(), info.getDisplayName());
            }
        });
        id2name.put("minecraft", "Minecraft");
    }

    public static String getModName(IForgeRegistryEntry<?> entry) {
        return getModName(entry.getRegistryName().getNamespace());
    }

    public static String getModName(String modId) {
        return id2name.getOrDefault(modId, modId);
    }
}
