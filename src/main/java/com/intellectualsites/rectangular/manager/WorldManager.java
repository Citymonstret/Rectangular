package com.intellectualsites.rectangular.manager;

import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.core.Region;
import com.intellectualsites.rectangular.core.WorldContainer;
import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This manages each world, and its quadrants
 *
 * @author Citymonstret
 */
public class WorldManager {

    @Getter
    private final HashMap<String, WorldContainer> worldContainers = new HashMap<>();

    public WorldManager(Collection<String> worlds) {
        for (String world : worlds) {
            WorldContainer temp = new WorldContainer(world);
            worldContainers.put(temp.getContainerID(), temp);
        }
        /*
        Remove bukkit specific code
        for (World world : Bukkit.getWorlds()) {
            WorldContainer temporary = new WorldContainer(world.getName());
            worldContainers.put(temporary.getContainerID(), temporary);
        }*/
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
            Region region = Rectangular.get().getRegionManager().getRegion(i);
            set.add(region);
        }
        return set;
    }
}
