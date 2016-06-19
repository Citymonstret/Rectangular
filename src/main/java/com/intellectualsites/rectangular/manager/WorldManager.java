package com.intellectualsites.rectangular.manager;

import com.intellectualsites.rectangular.CoreModule;
import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.core.ContainerFactory;
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
public class WorldManager extends ContainerFactory<WorldContainer> implements CoreModule {

    @Getter
    private final HashMap<String, WorldContainer> worldContainers = new HashMap<>();

    public WorldManager(Collection<String> worlds) {
        super('w');

        for (String world : worlds) {
            WorldContainer temp = new WorldContainer(world);
            worldContainers.put(temp.getWorldName(), temp);
        }
    }

    /**
     * Do not use this method, it should be cached
     *
     * @param worldName Name of the world
     * @return Set containing all regions
     */
    public Set<Region> getRegionsInWorld(String worldName) {
        WorldContainer container = getContainer(worldName);
        Set<Region> set = new HashSet<>();
        for (int i : container.getRegionIDs()) {
            Region region = Rectangular.get().getRegionManager().getRegion(i);
            set.add(region);
        }
        return set;
    }

    @Override
    public WorldContainer getContainer(String key) {
        if (key.startsWith("w:")) {
            key = key.replace("w:", "");
        }
        if (!hasContainer(key)) {
            return new WorldContainer("null");
        }
        return worldContainers.get(key);
    }

    @Override
    public boolean hasContainer(String key) {
        if (key.startsWith("w:")) {
            key = key.replace("w:", "");
        }
        return worldContainers.containsKey(key);
    }
}
