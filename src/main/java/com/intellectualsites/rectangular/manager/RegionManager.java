package com.intellectualsites.rectangular.manager;

import com.intellectualsites.rectangular.core.Quadrant;
import com.intellectualsites.rectangular.core.Rectangle;
import com.intellectualsites.rectangular.core.Region;
import com.intellectualsites.rectangular.core.RegionContainer;
import com.intellectualsites.rectangular.database.RectangularDB;
import com.intellectualsites.rectangular.vector.Vector2;

import java.util.*;

import static com.bergerkiller.bukkit.common.reflection.classes.BlockRef.id;

public class RegionManager {

    private WorldManager worldManager;

    private final Map<String, Integer> idMapping = new HashMap<>();
    private final Map<Integer, Region> regionMap = new HashMap<>();
    private final Map<Integer, Set<Integer>> layerMap = new HashMap<>();

    public RegionManager(WorldManager worldManager, RectangularDB database) {
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

    /**
     * Add or hot-swap a region
     * @param region Region to add, or update
     */
    public void addRegion(Region region) {
        if (!idMapping.containsKey(region.getContainerID())) {
            idMapping.put(region.getContainerID(), region.getId());
        }
        if (!regionMap.containsKey(region.getId())) {
            regionMap.put(region.getId(), region);
        }
        if (region.getOwningContainer().startsWith("w:")) {
            worldManager.getWorldContainers().get(region.getOwningContainer()).compileQuadrants(region);
        } else {
            regionMap.get(idMapping.get(region.getOwningContainer())).compileQuadrants(region);
        }
    }

    /**
     * Check if a rectangle overlaps any regions
     * in a given world
     *
     * @param world World in which the rectangle is located
     * @param rectangle Rectangle to check for
     *
     * @return A set containing all the regions that the rectangle
     * was overlapping with, which means that this will be empty if
     * there was no overlapping
     */
    public Set<Region> overlaps(String world, Rectangle rectangle) {
        // This will just make sure that we get all possible regions
        // As the min and the max might be in different world quadrants (unlikely)
        Quadrant minQ = worldManager.getWorldContainer(world).getContainerQuadrant(rectangle.getMin());
        Quadrant maxQ = worldManager.getWorldContainer(world).getContainerQuadrant(rectangle.getMax());

        Set<Region> regions = new HashSet<>();
        List<Integer> toCheck = new ArrayList<>();

        // Make sure that every region is only checked once
        minQ.getIds().stream().filter(i -> !toCheck.contains(i)).forEach(toCheck::add);
        maxQ.getIds().stream().filter(i -> !toCheck.contains(i)).forEach(toCheck::add);

        // A very simple overlap check
        for (int i : toCheck) {
            Region temp = getRegion(i);
            if (temp.getBoundingBox().overlaps(rectangle)) {
                regions.add(temp);
            }
        }

        // Yay
        return regions;
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
