package com.intellectualsites.rectangular.manager;

import com.intellectualsites.rectangular.CoreModule;
import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.bukkit.RectangularPlugin;
import com.intellectualsites.rectangular.core.*;
import com.intellectualsites.rectangular.data.RegionData;
import com.intellectualsites.rectangular.database.RectangularDB;
import com.intellectualsites.rectangular.event.impl.RegionManagerDoneEvent;
import com.intellectualsites.rectangular.vector.Vector2;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RegionManager implements CoreModule {

    private final ContainerManager containerManager;

    private final Map<String, Integer> idMapping = new ConcurrentHashMap<>();
    private final Map<Integer, Region> regionMap = new ConcurrentHashMap<>();

    public RegionManager(ContainerManager containerManager, RectangularDB database) {
        this.containerManager = containerManager;
    }

    public void load() {
        Set<Rectangle> temp = Rectangular.get().getDatabase().loadRectangles();
        Set<Region> regions = Rectangular.get().getDatabase().loadRegions();
        Set<RegionData> regionDatas = Rectangular.get().getDatabase().loadRegionData();

        for (Region region : regions) {
            region.setRectangles(temp.stream()
                    .filter(r -> r.getId() == region.getId()).collect(Collectors.toSet()));
            region.compile();
            regionMap.put(region.getId(), region);
            idMapping.put(region.getContainerID(), region.getId());
        }

        for (Region region : regionMap.values()) {
            Bukkit.broadcastMessage("Region container id: " + region.getOwningContainer());
            RegionContainer container = containerManager.getRegionContainer(region.getOwningContainer());;
            container.compileQuadrants(region);
            Bukkit.broadcastMessage("Debug for: " + region.getId());
            Bukkit.broadcastMessage("-------------------");
            Bukkit.broadcastMessage("Container ID: " + container.getContainerID());
            Bukkit.broadcastMessage("In container quadrant 1: " + container.getContainerQuadrants()[0].getIds().contains(region.getId()));
            Bukkit.broadcastMessage("In container quadrant 2: " + container.getContainerQuadrants()[1].getIds().contains(region.getId()));
            Bukkit.broadcastMessage("In container quadrant 3: " + container.getContainerQuadrants()[2].getIds().contains(region.getId()));
            Bukkit.broadcastMessage("In container quadrant 4: " + container.getContainerQuadrants()[3].getIds().contains(region.getId()));
            Bukkit.broadcastMessage("-------------------");
        }

        // Yay! <3
        for (RegionData data : regionDatas) {
            getRegion(data.getRegionID()).setData(data);
        }

        // tc == Temporary
        Logger logger = JavaPlugin.getPlugin(RectangularPlugin.class).getLogger();
        logger.info("Finished loading!");
        logger.info("Loaded " + temp.size() + " rectangles, making " + regions.size() + " regions!");
        // -tc == End of Temporary

        // Call the event, but this action is always ran async
        Rectangular.get().getServiceManager().runSync(() -> Rectangular.get().getEventManager().push(new RegionManagerDoneEvent(RegionManager.this)));
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
            ((WorldManager) containerManager.getContainerFactory('w')).getWorldContainers().get(region.getOwningContainer()).compileQuadrants(region);
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
        Quadrant minQ = containerManager.getRegionContainer('w', world).getContainerQuadrant(rectangle.getMin());
        Quadrant maxQ = containerManager.getRegionContainer('w', world).getContainerQuadrant(rectangle.getMax());

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
        Quadrant quadrant = containerManager.getRegionContainer('w', world).getContainerQuadrant(vector2);

        if (quadrant == null || quadrant.isEmpty()) {
            return null;
        }
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
