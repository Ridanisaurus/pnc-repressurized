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

package me.desht.pneumaticcraft.common.config.subconfig;

import java.io.File;
import java.io.IOException;

public interface IAuxConfig {
    /**
     * Get the filename (within the "pneumaticcraft/" folder in the top-level config directory) where this
     * config should be stored.
     *
     * @return the config file name
     */
    String getConfigFilename();

    /**
     * Called during the pre-init phase, with the top-level mod config file name (pneumaticcraft.cfg)
     *
     * @param file the config file name
     * @throws IOException if there is a problem reading/writing any files
     */
    void preInit(File file) throws IOException;

    /**
     * Called during the post-init phase, after the server has finished loading.
     *
     * @param file the config file name
     * @throws IOException if there is a problem reading/writing any files
     */
    void postInit(File file) throws IOException;

    /**
     * Clear the contents of this config when init'ing a new config file.
     * This is required for world-specific configs to avoid carrying configs between worlds when switching
     * worlds in single-player.
     */
    default void clear() {
    }

    /**
     * If true, use a world-local directory for this config, outside the normal config/ hierarchy. Used for
     * world-specific (rather than game-global) saved data.
     * @return true to use a world-specific directory, false to use the usual config directory
     */
    default boolean useWorldSpecificDir() {
        return false;
    }
}
