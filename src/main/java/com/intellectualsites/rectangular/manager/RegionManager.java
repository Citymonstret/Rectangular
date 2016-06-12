package com.intellectualsites.rectangular.manager;

import com.intellectualsites.rectangular.core.Quadrant;
import com.intellectualsites.rectangular.core.Region;
import com.intellectualsites.rectangular.core.RegionContainer;
import com.intellectualsites.rectangular.vector.Vector2;

import java.util.*;

public class RegionManager {

    private WorldManager worldManager;
    private final HashMap<String, Integer> idMapping = new HashMap<>();
    private final HashMap<Integer, Region> regionMap = new HashMap<>();

    public RegionManager(WorldManager worldManager) {
        this.worldManager = worldManager;

        Set<Region> regions = new HashSet<>(); // TODO: Load
        for (Region region : regions) {
            regionMap.put(region.getId(), region);
            idMapping.put(region.getContainerID(), region.getId());
        }

        HashMap<RegionContainer, Set<Region>> containerMapping = new HashMap<>();
        for (Region region : regionMap.values()) {
            RegionContainer container;
            if (region.getOwningContainer().startsWith("w:")) {
                container = worldManager.getWorldContainers().get(region.getOwningContainer());
            } else {
                container = regionMap.get(idMapping.get(region.getOwningContainer()));
            }

            if (!containerMapping.containsKey(container)) {
                containerMapping.put(container, new HashSet<>());
            }
            containerMapping.get(container).add(region);
        }

        for (Map.Entry<RegionContainer, Set<Region>> entry : containerMapping.entrySet()) {
            entry.getKey().compileQuadrants(entry.getValue());
        }
    }

    public Region getHighestLevelRegion(String world, Vector2 vector2) {
        Quadrant quadrant =  worldManager.getWorldContainer(world).getContainerQuadrant(vector2);
        for (int id : quadrant.getIds()) {
            if (regionMap.get(id).isInRegion(vector2)) {
                return regionMap.get(id);
            }
        }
        return null;
    }

    public Region getRegion(int i) {
        return regionMap.get(i);
    }
}
