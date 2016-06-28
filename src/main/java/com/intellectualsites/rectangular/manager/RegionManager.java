package com.intellectualsites.rectangular.manager;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.intellectualsites.rectangular.CoreModule;
import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.api.objects.Region;
import com.intellectualsites.rectangular.core.Quadrant;
import com.intellectualsites.rectangular.core.Rectangle;
import com.intellectualsites.rectangular.core.RegionContainer;
import com.intellectualsites.rectangular.event.impl.RegionManagerDoneEvent;
import com.intellectualsites.rectangular.vector.Vector2;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class RegionManager implements CoreModule {

    private final ContainerManager containerManager;

    private final Map<String, Integer> idMapping = new ConcurrentHashMap<>();
    private final Map<Integer, Region> regionMap = new ConcurrentHashMap<>();

    public void load() {
        ImmutableCollection<Rectangle> temp = Rectangular.get().getDatabase().loadRectangles();
        ImmutableCollection<Region> regions = Rectangular.get().getDatabase().loadRegions();

        for (final Region region : regions) {
            region.setRectangles(temp.stream()
                    .filter(r -> r.getId() == region.getId()).collect(Collectors.toSet()));
            region.compile();
            regionMap.put(region.getId(), region);
            idMapping.put(region.getContainerID(), region.getId());
        }

        for (Region region : regionMap.values()) {
            try {
                RegionContainer container = containerManager
                        .getRegionContainer(region.getOwningContainer());
                container.compileQuadrants(region);
            } catch (final Exception e) {
                Rectangular.get().getServiceManager().logger().warning("Failed to find container: " + region.getOwningContainer());
            }
        }

        for (final int regionID : regionMap.keySet()) {
            try {
                regionMap.get(regionID).setData(Rectangular.get().getDatabase().loadRegionData(regionID));
            } catch (final Exception e) {
                Rectangular.get().getServiceManager().logger().warning("Failed to load data for: " + regionID);
            }
        }

        // tc == Temporary
        Consumer<String> logger = Rectangular.get().getServiceManager().logger()::info; // Cheating <3
        logger.accept("Finished loading!");
        logger.accept("Loaded " + temp.size() + " rectangles, making " + regions.size() + " regions!");
        // -tc == End of Temporary

        // Call the event, but this action is always ran async
        Rectangular.get().getServiceManager().runSync(() ->
                Rectangular.get().getEventManager().push(new RegionManagerDoneEvent(RegionManager.this)));
    }

    /**
     * Add or hot-swap a region
     * @param region Region to add, or update
     */
    public void addRegion(@NonNull final Region region) {
        try {
            addRegion(region);
        } catch (final Exception e) {
            e.printStackTrace();
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
    public ImmutableList<Region> overlaps(@NonNull final String world, @NonNull final Rectangle rectangle) {
        // This will just make sure that we get all possible regions
        // As the min and the max might be in different world quadrants (unlikely)
        Quadrant minQ = containerManager.getRegionContainer('w', world).getContainerQuadrant(rectangle.getMin());
        Quadrant maxQ = containerManager.getRegionContainer('w', world).getContainerQuadrant(rectangle.getMax());

        List<Region> regions = new ArrayList<>();
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
        return ImmutableList.copyOf(regions);
    }


    /**
     * Get the top level region for the given coordinates
     * May return null, if the location isn't in a region
     *
     * @param world The world we're checking in
     * @param vector2 The coordinates
     *
     * @return Top level region | Null
     */
    public Region getHighestLevelRegion(@NonNull final String world, @NonNull final Vector2 vector2) {
        // Get the WorldContainer ('w' is the prefix for WorldContainers')
        // Then get the quadrant for the vector
        Quadrant quadrant = containerManager.getRegionContainer('w', world)
                .getContainerQuadrant(vector2);

        // If the quadrant is null (should never happen)
        // Or if it's empty, then we'll just return
        if (quadrant == null || quadrant.isEmpty()) {
            return null;
        }

        // Loop through all region IDs in the quadrant
        for (int id : quadrant.getIds()) {
            // Fetch the region based on its ID and check if
            // it contains the vector
            if (regionMap.get(id).isInRegion(vector2)) {
                return regionMap.get(id);
            }
        }

        // Nah, didn't find it :/
        return null;
    }

    public ImmutableCollection<Region> filterRegions(Predicate<Region> filter) {
        return ImmutableList.copyOf(getRegions().stream().filter(filter).collect(Collectors.toList()));
    }

    public ImmutableCollection<Region> getRegions() {
        return ImmutableSet.copyOf(regionMap.values());
    }

    /**
     * Get the region based on its ID
     * @see Region#getId() to fetch the region ID
     * @param i Region  ID
     * @return The region | Null
     */
    public Region getRegion(int i) {
        if (!regionMap.containsKey(i)) {
            return null;
        }
        return regionMap.get(i);
    }

    public void addRegionUnsafe(Region region) throws Exception {
        if (!region.isCompiled()) {
            region.compile();
        }

        if (!idMapping.containsKey(region.getContainerID())) {
            idMapping.put(region.getContainerID(), region.getId());
        }
        if (!regionMap.containsKey(region.getId())) {
            regionMap.put(region.getId(), region);
        }

        if (region.getOwningContainer().startsWith("w:")) {
            ((WorldManager) containerManager.getContainerFactory('w')).getWorldContainers().get(region.getOwningContainer().replace("w:", "")).compileQuadrants(region);
        } else {
            ((RegionContainer) regionMap.get(idMapping.get(region.getOwningContainer()))).compileQuadrants(region);
        }
    }
}
