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

package me.desht.pneumaticcraft.common.progwidgets;

import me.desht.pneumaticcraft.common.util.EntityFilter;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a pair of entity filters: a whitelist and a blacklist, as used by programming puzzle pieces.
 */
class EntityFilterPair<T extends IProgWidget & IEntityProvider> {
    private final T widget;
    private final EntityFilter entityWhitelist;
    private final EntityFilter entityBlacklist;
    private String errorWhite = "", errorBlack = "";

    EntityFilterPair(T widget) {
        this.widget = widget;
        entityWhitelist = getFilter(widget, true);
        entityBlacklist = getFilter(widget, false);
    }

    public static <T extends IProgWidget & IEntityProvider> void addErrors(T widget, List<ITextComponent> errors) {
        EntityFilterPair<T> filter = new EntityFilterPair<>(widget);
        if (!filter.errorWhite.isEmpty()) {
            errors.add(new StringTextComponent("Invalid whitelist filter: " + filter.errorWhite));
        }
        if (!filter.errorBlack.isEmpty()) {
            errors.add(new StringTextComponent("Invalid blacklist filter: " + filter.errorBlack));
        }
    }

    private EntityFilter getFilter(T widget, boolean whitelist) {
        try {
            return EntityFilter.fromProgWidget(widget, whitelist);
        } catch (IllegalArgumentException e) {
            if (whitelist) {
                errorWhite = e.getMessage();
                return EntityFilter.allow();
            } else {
                errorBlack = e.getMessage();
                return EntityFilter.deny();
            }
        }
    }

    boolean isEntityValid(Entity e) {
        return entityWhitelist.test(e) && !entityBlacklist.test(e);
    }

    List<Entity> getValidEntities(World world) {
        return getEntitiesInArea(
                (ProgWidgetArea) widget.getConnectedParameters()[0],
                (ProgWidgetArea) widget.getConnectedParameters()[widget.getParameters().size()],
                world
        );
    }

    private List<Entity> getEntitiesInArea(ProgWidgetArea whitelistWidget, ProgWidgetArea blacklistWidget, World world) {
        if (whitelistWidget == null) {
            return new ArrayList<>();
        }
        Set<Entity> entities = new HashSet<>();
        ProgWidgetArea widget = whitelistWidget;
        while (widget != null) {
            entities.addAll(widget.getEntitiesWithinArea(world, entityWhitelist));
            widget = (ProgWidgetArea) widget.getConnectedParameters()[0];
        }
        widget = blacklistWidget;
        while (widget != null) {
            widget.getEntitiesWithinArea(world, entityWhitelist).forEach(entities::remove);
            widget = (ProgWidgetArea) widget.getConnectedParameters()[0];
        }
        if (entityBlacklist != null) {
            entities.removeIf(entityBlacklist);
        }
        return new ArrayList<>(entities);
    }

}
