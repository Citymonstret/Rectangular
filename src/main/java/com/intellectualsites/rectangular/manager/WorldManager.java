package com.intellectualsites.rectangular.manager;

import com.intellectualsites.rectangular.core.Region;
import com.intellectualsites.rectangular.core.WorldContainer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class WorldManager {

    @Getter
    private final HashMap<String, WorldContainer> worldContainers = new HashMap<>();

    public WorldManager() {
        for (World world : Bukkit.getWorlds()) {
            WorldContainer temporary = new WorldContainer(world.getName());
            worldContainers.put(temporary.getContainerID(), temporary);
        }
    }

    public WorldContainer getWorldContainer(String worldName) {
        String containerID = WorldContainer.generateID(worldName);
        if (!worldContainers.containsKey(containerID)) {
            return new WorldContainer("null");
        }
        return worldContainers.get(containerID);
    }

    /**
     * Do not use this method, it should be cached
     *
     * @param worldName Name of the world
     * @return Set containing all regions
     */
    public Set<Region> getRegionsInWorld(String worldName) {
        WorldContainer container = getWorldContainer(worldName);
        Set<Region> set = new HashSet<>();
        for (int i : container.getRegionIDs()) {
            // TODO: Wat
            Region region = new RegionManager(this).getRegion(i);
            set.add(region);
        }
        return set;
    }
}
